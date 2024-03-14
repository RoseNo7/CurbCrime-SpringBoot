package com.roseno.curbcrime.controller;

import com.roseno.curbcrime.domain.User;
import com.roseno.curbcrime.dto.api.ApiResponse;
import com.roseno.curbcrime.dto.api.ApiResult;
import com.roseno.curbcrime.dto.auth.AuthRequest;
import com.roseno.curbcrime.service.AuthService;
import com.roseno.curbcrime.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인
     * @param session
     * @param authRequest   로그인 정보
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/auth/login")
    public ResponseEntity<ApiResponse<Object>> login(HttpSession session,
                                                     @RequestBody @Valid AuthRequest authRequest) {
        ApiResponse<Object> apiResponse;

        Optional<User> optUser = authService.authenticate(authRequest);

        if (optUser.isPresent()) {
            User user = optUser.get();

            SessionUtil.login(session, user);
            
            apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.SUCCESS.status())
                    .build();
        } else {
            apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.FAILED.status())
                    .message("아이디 또는 비밀번호가 틀립니다.")
                    .build();
        }

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 로그아웃
     * @param session
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/auth/logout")
    public ResponseEntity<ApiResponse<Object>> logout(HttpSession session) {
        if (SessionUtil.isLogin(session)) {
            SessionUtil.logout(session);

            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.SUCCESS.status())
                    .build();

            return ResponseEntity.ok(apiResponse);
        } else {
            ApiResponse<Object> responseDTO = ApiResponse.builder()
                    .code(HttpStatus.UNAUTHORIZED.value())
                    .status(ApiResult.ERROR.status())
                    .message("인증되지 않은 사용자입니다.")
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDTO);
        }
    }
}
