package com.roseno.curbcrime.controller;

import com.roseno.curbcrime.dto.api.ApiResponse;
import com.roseno.curbcrime.dto.api.ApiResult;
import com.roseno.curbcrime.dto.user.*;
import com.roseno.curbcrime.service.UserService;
import com.roseno.curbcrime.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

        Optional<UserInfoResponse> optUser = userService.createUser(userJoinRequest);

        if (optUser.isPresent()) {
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
    public ResponseEntity<ApiResponse<Object>> isUsedId(@PathVariable String id) {
        ApiResponse<Object> apiResponse;

        boolean isUsed = userService.isUsedId(id);

        if (isUsed) {
            apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.FAILED.status())
                    .message("이미 사용중인 아이디입니다.")
                    .build();
        } else {
            apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.SUCCESS.status())
                    .message("사용 가능한 아이디입니다.")
                    .build();
        }
        
        return ResponseEntity.ok(apiResponse);
    }
    
    /**
     * 아이디 찾기
     * @param userFindIdRequest     아이디 찾기 정보
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/accounts/id")
    public ResponseEntity<ApiResponse<Object>> findUserId(@RequestBody @Valid UserFindIdRequest userFindIdRequest) {
        UserFindIdResponse userResponse = userService.findUserId(userFindIdRequest);

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .status(ApiResult.SUCCESS.status())
                .data(userResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 비밀번호 찾기 인증번호 발급
     * @param userFindPasswordRequest       비밀번호 찾기 정보
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/accounts/password/create")
    public ResponseEntity<ApiResponse<Object>> createPasswordCipher(@RequestBody @Valid UserFindPasswordRequest userFindPasswordRequest) {
        String cipher = userService.createPasswordCipher(userFindPasswordRequest);
        SessionUtil.setPasswordCipher(cipher);

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .status(ApiResult.SUCCESS.status())
                .message("인증번호가 발급되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 비밀번호 변경 인증번호 확인
     * @param userCipherRequest
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/accounts/password/validate")
    public ResponseEntity<ApiResponse<Object>> validatePasswordCipher(@RequestBody @Valid UserCipherRequest userCipherRequest) {
        ApiResponse<Object> apiResponse;

        Optional<String> optCipher = SessionUtil.getPasswordCipher();

        if (optCipher.isPresent()) {
            String createdCipher = optCipher.get();
            String inputCipher = userCipherRequest.getCipher();

            if (createdCipher.equals(inputCipher)) {
                SessionUtil.removePasswordCipher();

                apiResponse = ApiResponse.builder()
                        .code(HttpStatus.OK.value())
                        .status(ApiResult.SUCCESS.status())
                        .message("인증번호가 확인되었습니다.")
                        .build();
            } else {
                apiResponse = ApiResponse.builder()
                        .code(HttpStatus.OK.value())
                        .status(ApiResult.FAILED.status())
                        .message("올바른 인증번호가 아닙니다.")
                        .build();
            }
        } else {
            apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.FAILED.status())
                    .message("인증번호 발급 이력이 없습니다.")
                    .build();
        }

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 나의 정보 조회
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/my-account")
    public ResponseEntity<ApiResponse<Object>> getMyInfo() {
        long idx = SessionUtil.getCurrentUserIdx().orElse(-1L);

        UserInfoResponse userResponse = userService.findUser(idx);

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .status(ApiResult.SUCCESS.status())
                .data(userResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 나의 정보 변경
     * @param userInfoRequest   회원정보
     * @return
     */
    @RequestMapping(method = RequestMethod.PATCH, value = "/my-account")
    public ResponseEntity<ApiResponse<Object>> modifyMyInfo(@RequestBody @Valid UserInfoRequest userInfoRequest) {
        long idx = SessionUtil.getCurrentUserIdx().orElse(-1L);

        userService.updateUser(idx, userInfoRequest);

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .status(ApiResult.SUCCESS.status())
                .message("회원정보가 변경되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }


    /**
     * 나의 비밀번호 확인
     * @param userPasswordRequest   비밀번호
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/my-account/password")
    public ResponseEntity<ApiResponse<Object>> isUsedPassword(@RequestBody UserPasswordRequest userPasswordRequest) {
        ApiResponse<Object> apiResponse;

        long idx = SessionUtil.getCurrentUserIdx().orElse(-1L);
        boolean isUsed = userService.isUsedPassword(idx, userPasswordRequest);

        if (isUsed) {
            apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.SUCCESS.status())
                    .message("현재 사용중인 비밀번호입니다.")
                    .build();
        } else {
            apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.FAILED.status())
                    .message("사용중인 비밀번호가 아닙니다.")
                    .build();
        }

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 나의 비밀번호 변경
     * @param userPasswordRequest   비밀번호
     * @return
     */
    @RequestMapping(method = RequestMethod.PATCH, value = "/my-account/password")
    public ResponseEntity<ApiResponse<Object>> updateMyPassword(@RequestBody @Valid UserPasswordRequest userPasswordRequest) {
        long idx = SessionUtil.getCurrentUserIdx().orElse(-1L);

        userService.updatePassword(idx, userPasswordRequest);

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .status(ApiResult.SUCCESS.status())
                .message("비밀번호가 변경되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 나의 계정 삭제
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE, value="/my-account")
    public ResponseEntity<ApiResponse<Object>> deleteMyAccount(HttpServletRequest request,
                                                               HttpServletResponse response) {
        long idx = SessionUtil.getCurrentUserIdx().orElse(-1L);

        userService.deleteUser(idx);
        SessionUtil.logout(request, response);

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .status(ApiResult.SUCCESS.status())
                .message("회원탈퇴가 완료되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
