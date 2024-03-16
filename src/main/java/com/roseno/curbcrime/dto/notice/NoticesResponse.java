package com.roseno.curbcrime.dto.notice;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 공지사항 목록
 */
@Getter
@Builder
@ToString
public class NoticesResponse {
    private long id;
    private String title;
    private int viewCount;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private UserResponse user;
}
