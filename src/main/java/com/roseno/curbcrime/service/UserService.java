package com.roseno.curbcrime.service;

import com.roseno.curbcrime.dto.user.*;

import java.util.Optional;

public interface UserService {
    UserInfoResponse findUser(long idx);                                                // 회원정보 조회
    UserFindIdResponse findUserId(UserFindIdRequest userFindIdRequest);                 // 아이디 찾기
    boolean isUsedId(String id);                                                        // 아이디 사용 여부 확인
    boolean isUsedPassword(long idx, UserPasswordRequest userPasswordRequest);          // 현재 비밀번호 확인

    Optional<UserInfoResponse> createUser(UserJoinRequest userJoinRequest);             // 회원가입
    String createPasswordCipher(UserFindPasswordRequest userFindPasswordRequest);       // 비밀번호 인증번호 생성

    void updateUser(long idx, UserInfoRequest userInfoRequest);                         // 회원정보 변경
    void updatePassword(long idx, UserPasswordRequest userPasswordRequest);             // 비밀번호 변경

    void deleteUser(long idx);           // 회원 삭제
}
