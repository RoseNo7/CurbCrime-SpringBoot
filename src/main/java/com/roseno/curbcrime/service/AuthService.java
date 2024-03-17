package com.roseno.curbcrime.service;

import com.roseno.curbcrime.domain.User;
import com.roseno.curbcrime.dto.auth.AuthRequest;

import java.util.Optional;

public interface AuthService {
    Optional<User> authenticate(AuthRequest authRequest);           // 로그인
}
