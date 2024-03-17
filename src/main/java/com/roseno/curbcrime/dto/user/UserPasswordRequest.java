package com.roseno.curbcrime.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.ToString;

/**
 * 비밀번호
 */
@Getter
@ToString
public class UserPasswordRequest {
    @NotBlank
    private String password;
}
