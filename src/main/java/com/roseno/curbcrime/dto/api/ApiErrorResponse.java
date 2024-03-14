package com.roseno.curbcrime.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * API 응답 실패
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {
    int code;
    String status;
    String message;
}
