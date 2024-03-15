package com.roseno.curbcrime.dto.user;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 아이디 찾기
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserFindIdResponse {
    String id;
    LocalDateTime createAt;
}
