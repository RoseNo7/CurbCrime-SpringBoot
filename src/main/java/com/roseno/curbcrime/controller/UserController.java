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
    
    /**
     * 아이디 찾기
     * @param userFindIdRequest     아이디 찾기 정보
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/accounts/id")
    public ResponseEntity<ApiResponse<Object>> findUserId(@RequestBody @Valid UserFindIdRequest userFindIdRequest) {
        ApiResponse<Object> apiResponse;

        Optional<UserFindIdResponse> optUser = userService.findUserId(userFindIdRequest);

        if (optUser.isPresent()) {
            UserFindIdResponse userResponse = optUser.get();

            apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.SUCCESS.status())
                    .data(userResponse)
                    .build();
        } else {
            apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.FAILED.status())
                    .message("가입하신 정보와 일치하지 않습니다. 다시 확인해주세요.")
                    .build();
        }

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 비밀번호 찾기 인증번호 발급
     * @param userFindPasswordRequest       비밀번호 찾기 정보
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/accounts/password/create")
    public ResponseEntity<ApiResponse<Object>> createPasswordCipher(@RequestBody @Valid UserFindPasswordRequest userFindPasswordRequest) {
        ApiResponse<Object> apiResponse;

        Optional<String> optCipher = userService.createPasswordCipher(userFindPasswordRequest);

        if (optCipher.isPresent()) {
            String cipher = optCipher.get();

            SessionUtil.setPasswordCipher(cipher);

            apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.SUCCESS.status())
                    .message("인증번호가 발급되었습니다.")
                    .build();
        } else {
            apiResponse = ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .status(ApiResult.FAILED.status())
                    .message("회원을 찾을 수 없습니다.")
                    .build();
        }

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
        Optional<Long> optIdx = SessionUtil.getCurrentUserIdx();
        Optional<UserInfoResponse> optUser = Optional.empty();

        if (optIdx.isPresent()) {
            long idx = optIdx.get();
            optUser = userService.findUser(idx);
        }

        if (optUser.isPresent()) {
            UserInfoResponse userResponse = optUser.get();

            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.SUCCESS.status())
                    .data(userResponse)
                    .build();

            return ResponseEntity.ok(apiResponse);
        } else {
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .status(ApiResult.FAILED.status())
                    .message("회원을 찾을 수 없습니다.")
                    .build();

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
    }

    /**
     * 나의 정보 변경
     * @param userInfoRequest   회원정보
     * @return
     */
    @RequestMapping(method = RequestMethod.PATCH, value = "/my-account")
    public ResponseEntity<ApiResponse<Object>> modifyMyInfo(@RequestBody @Valid UserInfoRequest userInfoRequest) {
        Optional<Long> optIdx = SessionUtil.getCurrentUserIdx();
        boolean isUpdated = false;

        if (optIdx.isPresent()) {
            long idx = optIdx.get();
            isUpdated = userService.updateUser(idx, userInfoRequest);
        }

        if (isUpdated) {
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.SUCCESS.status())
                    .message("회원정보가 변경되었습니다.")
                    .build();

            return ResponseEntity.ok(apiResponse);
        } else {
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .status(ApiResult.FAILED.status())
                    .message("회원을 찾을 수 없습니다.")
                    .build();

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
    }

    /**
     * 나의 비밀번호 확인
     * @param userPasswordRequest   비밀번호
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/my-account/password")
    public ResponseEntity<ApiResponse<Object>> isUsedPassword(@RequestBody UserPasswordRequest userPasswordRequest) {
        Optional<Long> optIdx = SessionUtil.getCurrentUserIdx();
        boolean isUsed = false;

        if (optIdx.isPresent()) {
            long idx = optIdx.get();
            isUsed = userService.isUsedPassword(idx, userPasswordRequest);
        }

        ApiResponse<Object> apiResponse;

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
        Optional<Long> optIdx = SessionUtil.getCurrentUserIdx();
        boolean isUpdated = false;

        if (optIdx.isPresent()) {
            long idx = optIdx.get();
            isUpdated = userService.updatePassword(idx, userPasswordRequest);
        }

        if (isUpdated) {
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.SUCCESS.status())
                    .message("비밀번호가 변경되었습니다.")
                    .build();

            return ResponseEntity.ok(apiResponse);
        } else {
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .status(ApiResult.FAILED.status())
                    .message("회원을 찾을 수 없습니다.")
                    .build();

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
    }

    /**
     * 나의 계정 삭제
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE, value="/my-account")
    public ResponseEntity<ApiResponse<Object>> deleteMyAccount(HttpServletRequest request,
                                                               HttpServletResponse response) {
        Optional<Long> optIdx = SessionUtil.getCurrentUserIdx();
        boolean isDeleted = false;

        if (optIdx.isPresent()) {
            long idx = optIdx.get();
            isDeleted = userService.deleteUser(idx);
        }

        if (isDeleted) {
            SessionUtil.logout(request, response);

            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.SUCCESS.status())
                    .message("회원탈퇴가 완료되었습니다.")
                    .build();

            return ResponseEntity.ok(apiResponse);
        } else {
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .status(ApiResult.FAILED.status())
                    .message("회원을 찾을 수 없습니다.")
                    .build();

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
    }
}
