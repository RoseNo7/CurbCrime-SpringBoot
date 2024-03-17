package com.roseno.curbcrime.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * 회원가입
 */
@Getter
@ToString
public class UserJoinRequest {
    @NotBlank
    @Size(min = 6, max = 20)
    private String id;

    @NotBlank
    @Size(min = 6, max = 20)
    private String password;

    @NotBlank
    @Size(min = 2, max = 20)
    private String name;

    @NotBlank
    @Email
    @Size(min = 2, max = 100)
    private String email;
}
