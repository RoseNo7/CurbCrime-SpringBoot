package com.roseno.curbcrime.dto.notice;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * 공지사항 목록
 */
@Getter
@Builder
@ToString
public class PageResponse {
    private int page;
    private int count;
    private int totalCount;

    private List<NoticesResponse> notices;
}
