package com.roseno.curbcrime.dto.notice;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * 작성자
 */
@Getter
@Builder
@ToString
public class UserResponse {
    private Long idx;
    private String id;
    private String role;
}
