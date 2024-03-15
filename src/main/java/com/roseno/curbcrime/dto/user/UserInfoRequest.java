package com.roseno.curbcrime.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserInfoRequest {
    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;
}
