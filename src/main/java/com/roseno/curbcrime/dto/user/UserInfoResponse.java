package com.roseno.curbcrime.dto.user;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
public class UserInfoResponse {
    long idx;
    String id;
    String name;
    String email;
    String role;
    LocalDateTime createAt;
}
