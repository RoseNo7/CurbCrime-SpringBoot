package com.roseno.curbcrime.service;

import com.roseno.curbcrime.dto.user.UserFindIdRequest;
import com.roseno.curbcrime.dto.user.UserFindIdResponse;
import com.roseno.curbcrime.dto.user.UserFindPasswordRequest;
import com.roseno.curbcrime.dto.user.UserJoinRequest;

import java.util.Optional;

public interface UserService {
    Optional<UserFindIdResponse> findUserId(UserFindIdRequest userFindIdRequest);       // 아이디 찾기
    boolean isUsedId(String id);                                                        // 아이디 사용 여부 확인

    Optional<Long> createUser(UserJoinRequest userJoinRequest);                                 // 회원가입
    Optional<String> createPasswordCipher(UserFindPasswordRequest userFindPasswordRequest);     // 비밀번호 인증번호 생성
}
