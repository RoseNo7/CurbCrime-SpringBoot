package com.roseno.curbcrime.util;

import com.roseno.curbcrime.model.Principal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Optional;

public class SessionUtil {
    private static final int SCOPE = RequestAttributes.SCOPE_SESSION;

    private static final String SESSION_KEY_PASSWORD_CIPHER = "PASSWORD_CIPHER";

    /**
     * 세션 제거
     */
    public static void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, authentication);
    }

    /**
     * 현재 사용자의 회원번호를 조회
     * @return      회원번호
     */
    public static Optional<Long> getCurrentUserIdx() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return Optional.empty();
        }

        Long idx = null;

        if (authentication.getPrincipal() instanceof UserDetails) {
            Principal principal = (Principal) authentication.getPrincipal();
            idx = principal.getIdx();
        }

        return Optional.ofNullable(idx);
    }

    /**
     * 세션에서 정보 조회
     * @param key
     * @return
     */
    public static Object getAttribute(String key) {
        return RequestContextHolder.currentRequestAttributes().getAttribute(key, SCOPE);
    }

    /**
     * 세션에서 비밀번호 인증버호 조회
     * @return      비밀번호 인증번호
     */
    public static Optional<String> getPasswordCipher() {
        return Optional.ofNullable((String) getAttribute(SESSION_KEY_PASSWORD_CIPHER));
    }

    /**
     * 세션에 정보 저장
     * @param key
     * @param value
     */
    public static void setAttribute(String key, String value) {
        RequestContextHolder.currentRequestAttributes().setAttribute(key, value, SCOPE);
    }
    
    /**
     * 세션에 비밀번호 인증번호 저장
     * @param cipher    비밀번호 인증번호
     */
    public static void setPasswordCipher(String cipher) {
        setAttribute(SESSION_KEY_PASSWORD_CIPHER, cipher);
    }

    /**
     * 세션에서 정보 삭제
     * @param key
     */
    public static void removeAttribute(String key) {
        RequestContextHolder.currentRequestAttributes().removeAttribute(key, SCOPE);
    }

    /**
     * 세션에서 비밀번호 인증번호 삭제
     */
    public static void removePasswordCipher() {
        removeAttribute(SESSION_KEY_PASSWORD_CIPHER);
    }
}
