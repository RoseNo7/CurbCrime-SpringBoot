package com.roseno.curbcrime.dto.user;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 아이디 찾기
 */
@Getter
@Builder
@ToString
public class UserFindIdResponse {
    private String id;
    private LocalDateTime createAt;
}
