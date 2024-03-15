package com.roseno.curbcrime.controller;

import com.roseno.curbcrime.dto.api.ApiResponse;
import com.roseno.curbcrime.dto.api.ApiResult;
import com.roseno.curbcrime.dto.user.UserJoinRequest;
import com.roseno.curbcrime.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 아이디 중복 확인
     * @param id    아이디
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/accounts/{id}/exists")
    public ResponseEntity<ApiResponse<Object>> isUsedId (@PathVariable String id) {
        ApiResponse<Object> apiResponse;

        boolean isUsed = userService.isUsedId(id);

        if (!isUsed) {
            apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.SUCCESS.status())
                    .message("사용 가능한 아이디입니다.")
                    .build();
        } else {
            apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.FAILED.status())
                    .message("이미 사용중인 아이디입니다.")
                    .build();
        }
        
        return ResponseEntity.ok(apiResponse);
    }
}
