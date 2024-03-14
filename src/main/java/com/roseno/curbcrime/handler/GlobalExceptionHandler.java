package com.roseno.curbcrime.handler;

import com.roseno.curbcrime.dto.api.ApiErrorResponse;
import com.roseno.curbcrime.dto.api.ApiResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * 요청 데이터 유효성 예외
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .code(status.value())
                .status(ApiResult.ERROR.status())
                .message("입력 값을 확인해주세요.")
                .build();

        return ResponseEntity.badRequest().body(apiErrorResponse);
    }
}
