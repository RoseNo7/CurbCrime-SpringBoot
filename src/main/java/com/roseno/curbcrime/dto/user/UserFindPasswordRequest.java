package com.roseno.curbcrime.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.ToString;

/**
 * 비밀번호 찾기
 */
@Getter
@ToString
public class UserFindPasswordRequest {
    @NotBlank
    private String id;

    @NotBlank
    @Email
    private String email;
}
