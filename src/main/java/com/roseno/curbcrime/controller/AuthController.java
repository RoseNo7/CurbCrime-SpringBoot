package com.roseno.curbcrime.controller;

import com.roseno.curbcrime.dto.api.ApiResponse;
import com.roseno.curbcrime.dto.api.ApiResult;
import com.roseno.curbcrime.dto.auth.AuthRequest;
import com.roseno.curbcrime.service.AuthService;
import com.roseno.curbcrime.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인
     * @param authRequest   로그인 정보
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/auth/login")
    public ResponseEntity<ApiResponse<Object>> login(HttpServletRequest request,
                                                     HttpServletResponse response,
                                                     @RequestBody @Valid AuthRequest authRequest) {
        authService.authenticate(request, response, authRequest);

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .status(ApiResult.SUCCESS.status())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 로그아웃
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/auth/logout")
    public ResponseEntity<ApiResponse<Object>> logout(HttpServletRequest request,
                                                      HttpServletResponse response) {
        SessionUtil.logout(request, response);

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .status(ApiResult.SUCCESS.status())
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
