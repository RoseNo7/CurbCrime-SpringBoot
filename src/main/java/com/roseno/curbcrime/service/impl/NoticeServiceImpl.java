package com.roseno.curbcrime.service.impl;

import com.roseno.curbcrime.domain.Notice;
import com.roseno.curbcrime.domain.User;
import com.roseno.curbcrime.dto.notice.*;
import com.roseno.curbcrime.exception.NotFoundException;
import com.roseno.curbcrime.exception.ServiceException;
import com.roseno.curbcrime.repository.NoticeRepository;
import com.roseno.curbcrime.service.NoticeService;
import com.roseno.curbcrime.specification.NoticeSpecification;
import com.roseno.curbcrime.util.UniqueKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {
    private static final int NOTICE_ID_LENGTH = 8;

    private final NoticeRepository noticeRepository;

    /**
     * 공지사항 목록 조회
     * @param page              페이지 번호
     * @param count             목록 개수
     * @param sortOption        정렬 옵션
     * @param searchOption      검색 옵션
     * @param keyword           검색어
     * @return                  공지사항 목록
     */
    @Transactional(readOnly = true)
    public PageResponse findNotices(int page,
                                    int count,
                                    String sortOption,
                                    String searchOption,
                                    String keyword) {
        int pageNumber = page - 1;
        Sort sort = toSortColumn(sortOption);                                                       // 정렬
        Specification<Notice> spec = NoticeSpecification.bySearchOption(searchOption, keyword);     // 검색
        Pageable pageable = PageRequest.of(pageNumber, count, sort);                                // 페이징
        Page<Notice> pagination = noticeRepository.findAll(spec, pageable);

        return PageResponse.builder()
                .page(page)
                .count(pagination.getContent().size())
                .totalCount((int) pagination.getTotalElements())
                .notices(pagination.getContent().stream()
                        .map(this::mapToNoticesResponse)
                        .toList()
                )
                .build();
    }

    /**
     * 공지사항 조회
     * @param id    공지사항 아이디
     * @return      공지사항
     */
    @Override
    @Transactional(readOnly = true)
    public NoticeResponse findNotice(long id) {
        Notice notice = noticeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("공지사항을 조회할 수 없습니다."));

        NoticeResponse.NoticeResponseBuilder builder = NoticeResponse.builder();
        builder.id(notice.getId());
        builder.title(notice.getTitle());
        builder.content(notice.getContent());
        builder.viewCount(notice.getViewCount());
        builder.createAt(notice.getCreateAt());
        builder.updateAt(notice.getUpdateAt());

        User user = notice.getUser();
        if (user != null) {
            UserResponse userResponse = UserResponse.builder()
                    .idx(user.getIdx())
                    .id(user.getId())
                    .role(user.getRole())
                    .build();

            builder.user(userResponse);
        }

        return builder.build();
    }

    /**
     * 공지사항 생성
     * @param userIdx           회원번호
     * @param noticeRequest     공지사항
     * @return                  공지사항 아이디
     */
    @Override
    public Optional<NoticeResponse> createNotice(long userIdx, NoticeRequest noticeRequest) {
        Notice notice = Notice.builder()
                .id(generateNoticeId())
                .title(noticeRequest.getTitle())
                .content(noticeRequest.getContent())
                .user(User.builder()
                        .idx(userIdx)
                        .build()
                )
                .build();

        try {
            Notice savedNotice = noticeRepository.save(notice);

            NoticeResponse noticeResponse = NoticeResponse.builder()
                    .id(savedNotice.getId())
                    .build();

            return Optional.ofNullable(noticeResponse);
        } catch (DataAccessException e) {
            throw new ServiceException("요청을 처리하는 동안 오류가 발생했습니다. 나중에 다시 시도해주세요.");
        }
    }

    /**
     * 공지사항 수정
     * @param id                공지사항 아이디
     * @param noticeRequest     공지사항
     */
    @Override
    @Transactional
    public void updateNotice(long id, NoticeRequest noticeRequest) {
        Notice notice = noticeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("공지사항을 찾을 수 없습니다."));

        notice.setTitle(noticeRequest.getTitle());
        notice.setContent(noticeRequest.getContent());
    }

    /**
     * 공지사항 조회수 증가
     * @param id        공지사항 아이디
     */
    @Override
    @Transactional
    public void incrementNoticeView(long id) {
        if (noticeRepository.existsByIdAndIsDeletedFalse(id)) {
            noticeRepository.increaseViewCount(id);
        } else {
            throw new NotFoundException("공지사항을 찾을 수 없습니다.");
        }
    }

    /**
     * 공지사항 삭제
     * @param id    공지사항 아이디
     */
    @Override
    @Transactional
    public void deleteNotice(long id) {
        Notice notice = noticeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("공지사항을 찾을 수 없습니다."));

        notice.setDeleted(true);
        notice.setDeleteAt(LocalDateTime.now());
    }

    /**
     * 공지사항 응답 객체로 변경
     * @param notice    공지사항
     * @return          공지사항 응답 객체
     */
    public NoticesResponse mapToNoticesResponse(Notice notice) {
        NoticesResponse.NoticesResponseBuilder builder = NoticesResponse.builder();
        builder.id(notice.getId());
        builder.title(notice.getTitle());
        builder.viewCount(notice.getViewCount());
        builder.createAt(notice.getCreateAt());
        builder.updateAt(notice.getUpdateAt());

        User user = notice.getUser();
        if (user != null) {
            UserResponse userResponse = UserResponse.builder()
                    .idx(user.getIdx())
                    .id(user.getId())
                    .role(user.getRole())
                    .build();

            builder.user(userResponse);
        }

        return builder.build();
    }

    /**
     * 정렬 옵션을 컬럼으로 변경
     * @param sortOption    정렬 옵션
     * @return              정렬 컬럼
     */
    public Sort toSortColumn(String sortOption) {
        return switch (sortOption) {
            case SORT_OPTION_LATEST -> Sort.by(Sort.Direction.DESC, "createAt");
            case SORT_OPTION_VIEW -> Sort.by(Sort.Direction.DESC, "viewCount", "createAt");
            default -> Sort.by(Sort.Direction.DESC, "createAt");
        };
    }

    /**
     * 공지사항 아이디 생성
     * @return      공지사항 아이디
     */
    public long generateNoticeId() {
        long id;

        do {
            id = UniqueKeyGenerator.generateNumeric(NOTICE_ID_LENGTH);
        } while(noticeRepository.existsById(id));

        return id;
    }
}
