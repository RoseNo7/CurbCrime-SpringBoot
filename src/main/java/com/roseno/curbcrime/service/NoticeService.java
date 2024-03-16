package com.roseno.curbcrime.service;

import com.roseno.curbcrime.dto.notice.NoticeRequest;
import com.roseno.curbcrime.dto.notice.NoticeResponse;
import com.roseno.curbcrime.dto.notice.PageResponse;

import java.util.Optional;

public interface NoticeService {
                                                                    // 정렬 옵션
    String SORT_OPTION_LATEST = "LATEST";                           // 최신순
    String SORT_OPTION_VIEW = "VIEW";                               // 조회수순

    PageResponse findNotices(int page, int count, String orderOption, String searchOption, String keyword);     // 공지사항 목록 조회
    Optional<NoticeResponse> findNotice(long id);                                                               // 공지사항 조회

    Optional<Long> createNotice(long idx, NoticeRequest noticeRequest);         // 공지사항 생성
}
