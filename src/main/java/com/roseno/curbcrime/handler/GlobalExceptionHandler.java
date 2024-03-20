package com.roseno.curbcrime.handler;

import com.roseno.curbcrime.dto.api.ApiErrorResponse;
import com.roseno.curbcrime.dto.api.ApiResult;
import com.roseno.curbcrime.exception.NotFoundException;
import com.roseno.curbcrime.exception.ServiceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
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

    /**
     * 로그인 예외
     * @param ex
     * @return
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .code(HttpStatus.UNAUTHORIZED.value())
                .status(ApiResult.FAILED.status())
                .message("아이디 또는 비밀번호가 틀립니다.")
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiErrorResponse);
    }

    /**
     * 서비스 예외 처리
     * @param ex
     * @return
     */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiErrorResponse> handleServiceException(ServiceException ex) {
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .status(ApiResult.ERROR.status())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiErrorResponse);
    }

    /**
     * Not Found Exception
     * @param ex
     * @return
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFoundException(NotFoundException ex) {
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .status(ApiResult.FAILED.status())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiErrorResponse);
    }
}
