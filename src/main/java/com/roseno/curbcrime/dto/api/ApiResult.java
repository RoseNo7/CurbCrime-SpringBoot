package com.roseno.curbcrime.dto.api;

/**
 * API 응답 결과
 */
public enum ApiResult {
    SUCCESS("success"),
    FAILED("failed"),
    ERROR("error");

    private final String status;

    ApiResult(String status) {
        this.status = status;
    }

    public String status() {
        return this.status;
    }
}
