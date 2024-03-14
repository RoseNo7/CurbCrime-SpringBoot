package com.roseno.curbcrime.service;

import com.roseno.curbcrime.dto.user.UserJoinRequest;

import java.util.Optional;

public interface UserService {
    Optional<Long> createUser(UserJoinRequest userJoinRequest);             // 회원가입
}
