package com.roseno.curbcrime.dto.api;

import lombok.Builder;
import lombok.Getter;

/**
 * API 응답 성공
 */
@Getter
@Builder
public class ApiResponse<T> {
    private int code;
    private String status;
    private String message;
    private T data;
}
