package com.roseno.curbcrime.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.ToString;

/**
 * 아이디 찾기
 */
@Getter
@ToString
public class UserFindIdRequest {
    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;
}
