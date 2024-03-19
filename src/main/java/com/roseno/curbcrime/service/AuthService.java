package com.roseno.curbcrime.service;

import com.roseno.curbcrime.dto.auth.AuthRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    void authenticate(HttpServletRequest request, HttpServletResponse response, AuthRequest authRequest);       // 로그인
}
