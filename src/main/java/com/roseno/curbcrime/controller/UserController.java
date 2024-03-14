package com.roseno.curbcrime.controller;

import com.roseno.curbcrime.dto.api.ApiResponse;
import com.roseno.curbcrime.dto.api.ApiResult;
import com.roseno.curbcrime.dto.user.UserJoinRequest;
import com.roseno.curbcrime.service.UserService;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원가입
     * @param userJoinRequest   회원가입 정보
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/accounts")
    public ResponseEntity<ApiResponse<Object>> addUser(@RequestBody @Valid UserJoinRequest userJoinRequest) {
        ApiResponse<Object> apiResponse;

        Optional<Long> optIdx = userService.createUser(userJoinRequest);

        if (optIdx.isPresent()) {
             apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.SUCCESS.status())
                    .message("회원가입이 완료되었습니다.")
                    .build();
        } else {
            apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.FAILED.status())
                    .message("회원가입에 실패하였습니다.")
                    .build();
        }

        return ResponseEntity.ok(apiResponse);
    }
}
