package com.roseno.curbcrime.dto.user;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 회원정보
 */
@Getter
@Builder
@ToString
public class UserInfoResponse {
    private long idx;
    private String id;
    private String name;
    private String email;
    private String role;
    private LocalDateTime createAt;
}
