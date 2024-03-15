package com.roseno.curbcrime.controller;

import com.roseno.curbcrime.dto.api.ApiResponse;
import com.roseno.curbcrime.dto.api.ApiResult;
import com.roseno.curbcrime.dto.user.*;
import com.roseno.curbcrime.service.UserService;
import com.roseno.curbcrime.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
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
     * @param session
     * @param userFindPasswordRequest       비밀번호 찾기 정보
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/accounts/password/create")
    public ResponseEntity<ApiResponse<Object>> createPasswordCipher(HttpSession session,
                                                                    @RequestBody @Valid UserFindPasswordRequest userFindPasswordRequest) {
        ApiResponse<Object> apiResponse;

        Optional<String> optCipher = userService.createPasswordCipher(userFindPasswordRequest);

        if (optCipher.isPresent()) {
            String cipher = optCipher.get();

            SessionUtil.setPasswordCipher(session, cipher);

            apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.SUCCESS.status())
                    .message("인증번호가 발급되었습니다.")
                    .build();
        } else {
            apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.FAILED.status())
                    .message("인증번호 발급에 실패하였습니다.")
                    .build();
        }

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 비밀번호 변경 인증번호 확인
     * @param session
     * @param userCipherRequest
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/accounts/password/validate")
    public ResponseEntity<ApiResponse<Object>> validatePasswordCipher(HttpSession session,
                                                                      @RequestBody @Valid UserCipherRequest userCipherRequest) {
        ApiResponse<Object> apiResponse;

        Optional<String> optCipher = SessionUtil.getPasswordCipher(session);

        if (optCipher.isPresent()) {
            String createdCipher = optCipher.get();
            String inputCipher = userCipherRequest.getCipher();

            if (createdCipher.equals(inputCipher)) {
                SessionUtil.removePasswordCipher(session);

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
     * @param session
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/my-account")
    public ResponseEntity<ApiResponse<Object>> getMyInfo(HttpSession session) {
        Optional<Long> optIdx = SessionUtil.getCurrentUserIdx(session);

        if (optIdx.isEmpty()) {
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.UNAUTHORIZED.value())
                    .status(ApiResult.ERROR.status())
                    .message("인증되지 않은 사용자입니다.")
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        }

        long idx = optIdx.get();
        Optional<UserInfoResponse> optUser = userService.findUser(idx);

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
     * @param session
     * @param userInfoRequest   회원정보
     * @return
     */
    @RequestMapping(method = RequestMethod.PATCH, value = "/my-account")
    public ResponseEntity<ApiResponse<Object>> modifyMyInfo(HttpSession session,
                                                            @RequestBody @Valid UserInfoRequest userInfoRequest) {
        Optional<Long> optIdx = SessionUtil.getCurrentUserIdx(session);

        if (optIdx.isEmpty()) {
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.UNAUTHORIZED.value())
                    .status(ApiResult.ERROR.status())
                    .message("인증되지 않은 사용자입니다.")
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        }

        long idx = optIdx.get();
        boolean isUpdated = userService.updateUser(idx, userInfoRequest);

        if (isUpdated) {
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.SUCCESS.status())
                    .data("회원정보가 변경되었습니다.")
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
     * @param session
     * @param userPasswordRequest   비밀번호
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/my-account/password")
    public ResponseEntity<ApiResponse<Object>> isUsedPassword(HttpSession session,
                                                              @RequestBody UserPasswordRequest userPasswordRequest) {
        Optional<Long> optIdx = SessionUtil.getCurrentUserIdx(session);

        if (optIdx.isEmpty()) {
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.UNAUTHORIZED.value())
                    .status(ApiResult.ERROR.status())
                    .message("인증되지 않은 사용자입니다.")
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        }

        ApiResponse<Object> apiResponse;

        long idx = optIdx.get();
        boolean isUsed = userService.isUsedPassword(idx, userPasswordRequest);

        if (isUsed) {
            apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.SUCCESS.status())
                    .data("현재 사용중인 비밀번호입니다.")
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
     * @param session
     * @param userPasswordRequest   비밀번호
     * @return
     */
    @RequestMapping(method = RequestMethod.PATCH, value = "/my-account/password")
    public ResponseEntity<ApiResponse<Object>> updateMyPassword(HttpSession session,
                                                                @RequestBody @Valid UserPasswordRequest userPasswordRequest) {
        Optional<Long> optIdx = SessionUtil.getCurrentUserIdx(session);

        if (optIdx.isEmpty()) {
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.UNAUTHORIZED.value())
                    .status(ApiResult.ERROR.status())
                    .message("인증되지 않은 사용자입니다.")
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        }

        long idx = optIdx.get();
        boolean isUpdated = userService.updatePassword(idx, userPasswordRequest);

        if (isUpdated) {
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.SUCCESS.status())
                    .data("비밀번호가 변경되었습니다.")
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
     * @param session
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE, value="/my-account")
    public ResponseEntity<ApiResponse<Object>> deleteMyAccount(HttpSession session) {
        Optional<Long> optIdx = SessionUtil.getCurrentUserIdx(session);

        if (optIdx.isEmpty()) {
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.UNAUTHORIZED.value())
                    .status(ApiResult.ERROR.status())
                    .message("인증되지 않은 사용자입니다.")
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        }

        long idx = optIdx.get();
        boolean isDeleted = userService.deleteUser(idx);

        if (isDeleted) {
            SessionUtil.logout(session);

            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.SUCCESS.status())
                    .data("회원탈퇴가 완료되었습니다.")
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
