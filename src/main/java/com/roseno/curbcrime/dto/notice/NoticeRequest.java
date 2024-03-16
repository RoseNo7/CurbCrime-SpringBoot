package com.roseno.curbcrime.dto.notice;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class NoticeRequest {
    @NotBlank
    String title;

    @NotBlank
    String content;
}
