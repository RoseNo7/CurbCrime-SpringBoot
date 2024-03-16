package com.roseno.curbcrime.service.impl;

import com.roseno.curbcrime.domain.Notice;
import com.roseno.curbcrime.dto.notice.NoticeResponse;
import com.roseno.curbcrime.dto.notice.NoticesResponse;
import com.roseno.curbcrime.dto.notice.PageResponse;
import com.roseno.curbcrime.dto.notice.UserResponse;
import com.roseno.curbcrime.exception.ServiceException;
import com.roseno.curbcrime.mapper.NoticeMapper;
import com.roseno.curbcrime.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper noticeMapper;

    /**
     * 공지사항 목록 조회
     * @param page              페이지 번호
     * @param count             목록 개수
     * @param sortOption        정렬 옵션
     * @param searchOption      검색 옵션
     * @param keyword           검색어
     * @return                  공지사항 목록
     */
    public PageResponse findNotices(int page,
                                    int count,
                                    String sortOption,
                                    String searchOption,
                                    String keyword) {
        try {
            int offset = (page - 1) * count;
            int limit = count;
            int totalCount = noticeMapper.findNoticeCount(searchOption, keyword);
            sortOption = toSortColumn(sortOption);

            List<Notice> notices = noticeMapper.findNotices(offset, limit, sortOption, searchOption, keyword);
            List<NoticesResponse> noticesResponses = notices.stream()
                    .map(notice -> NoticesResponse.builder()
                            .id(notice.getId())
                            .title(notice.getTitle())
                            .viewCount(notice.getViewCount())
                            .createAt(notice.getCreateAt())
                            .updateAt(notice.getUpdateAt())
                            .user(UserResponse.builder()
                                    .idx(notice.getUser().getIdx())
                                    .id(notice.getUser().getId())
                                    .role(notice.getUser().getRole())
                                    .build())
                            .build()
                    )
                    .toList();

            return PageResponse.builder()
                    .page(page)
                    .count(count)
                    .totalCount(totalCount)
                    .notices(noticesResponses)
                    .build();

        } catch (DataAccessException e) {
            throw new ServiceException("요청을 처리하는 동안 오류가 발생했습니다. 나중에 다시 시도해주세요.");
        }
    }

    /**
     * 공지사항 조회
     * @param id    공지사항 아이디
     * @return      공지사항
     */
    @Override
    public Optional<NoticeResponse> findNotice(long id) {
        try {
            NoticeResponse noticeResponse = null;

            Optional<Notice> optNotice = noticeMapper.findNoticeById(id);

            if (optNotice.isPresent()) {
                Notice notice = optNotice.get();

                noticeResponse = NoticeResponse.builder()
                        .id(notice.getId())
                        .title(notice.getTitle())
                        .content(notice.getContent())
                        .createAt(notice.getCreateAt())
                        .updateAt(notice.getUpdateAt())
                        .user(UserResponse.builder()
                                .idx(notice.getUser().getIdx())
                                .id(notice.getUser().getId())
                                .role(notice.getUser().getRole())
                                .build()
                        )
                        .build();
            }

            return Optional.ofNullable(noticeResponse);
        } catch (DataAccessException e) {
            throw new ServiceException("요청을 처리하는 동안 오류가 발생했습니다. 나중에 다시 시도해주세요.");
        }
    }

    /**
     * 정렬 옵션을 컬럼으로 변경
     * @param sortOption    정렬 옵션
     * @return              정렬 컬럼
     */
    public String toSortColumn(String sortOption) {
        return switch (sortOption) {
            case SORT_OPTION_LATEST -> "create_at DESC";
            case SORT_OPTION_VIEW -> "view_count DESC, create_at DESC";
            default -> "create_at DESC";
        };
    }
}
