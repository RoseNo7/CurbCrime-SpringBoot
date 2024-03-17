package com.roseno.curbcrime.dto.api;

import lombok.Builder;
import lombok.Getter;

/**
 * API 응답 실패
 */
@Getter
@Builder
public class ApiErrorResponse {
    private int code;
    private String status;
    private String message;
}
