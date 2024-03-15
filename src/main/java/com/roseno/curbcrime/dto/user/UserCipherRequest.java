package com.roseno.curbcrime.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.ToString;

/**
 * 인증번호
 */
@Getter
@ToString
public class UserCipherRequest {
    @NotBlank
    private String cipher;
}
