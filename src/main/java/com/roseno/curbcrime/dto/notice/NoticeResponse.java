package com.roseno.curbcrime.dto.notice;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 공지사항
 */
@Getter
@Builder
@ToString
public class NoticeResponse {
    private long id;
    private String title;
    private String content;
    private int viewCount;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private UserResponse user;
}
