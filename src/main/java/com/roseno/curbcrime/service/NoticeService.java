package com.roseno.curbcrime.service;

import com.roseno.curbcrime.dto.notice.PageResponse;

import java.util.List;

public interface NoticeService {
                                                                    // 정렬 옵션
    String SORT_OPTION_LATEST = "LATEST";                           // 최신순
    String SORT_OPTION_VIEW = "VIEW";                               // 조회수순

    PageResponse findNotices(int page, int count, String orderOption, String searchOption, String keyword);     // 공지사항 목록 조회
}
