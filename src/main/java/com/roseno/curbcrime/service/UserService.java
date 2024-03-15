package com.roseno.curbcrime.service;

import com.roseno.curbcrime.dto.user.UserJoinRequest;

import java.util.Optional;

public interface UserService {
    boolean isUsedId(String id);            // 아이디 사용 여부 확인
    Optional<Long> createUser(UserJoinRequest userJoinRequest);             // 회원가입
}
