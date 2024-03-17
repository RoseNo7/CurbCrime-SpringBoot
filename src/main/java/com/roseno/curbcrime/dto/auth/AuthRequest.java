package com.roseno.curbcrime.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * 로그인
 */
@Getter
@Builder
@ToString
public class AuthRequest {
    @NotBlank
    private String id;

    @NotBlank
    private String password;
}
