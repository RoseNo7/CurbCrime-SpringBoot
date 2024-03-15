package com.roseno.curbcrime.util;

import com.roseno.curbcrime.domain.User;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

public class SessionUtil {

    private static final String SESSION_KEY_USER = "USER";
    private static final String SESSION_KEY_PASSWORD_CIPHER = "PASSWORD_CIPHER";

    /**
     * 세션 로그인
     * @param session
     * @param user      회원정보
     */
    public static void login(HttpSession session, User user) {
        setUser(session, user);
    }

    /**
     * 로그인 여부 확인
     * @param session
     * @return          로그인 여부
     */
    public static boolean isLogin(HttpSession session) {
        return getUser(session).isPresent();
    }

    /**
     * 세션 제거
     * @param session
     */
    public static void logout(HttpSession session) {
        session.invalidate();
    }

    /**
     * 세션에서 정보 조회
     * @param session
     * @param key
     * @return
     */
    public static Object getAttribute(HttpSession session, String key) {
        return session.getAttribute(key);
    }

    /**
     * 세션에서 회원정보 조회
     * @param session
     * @return          회원정보
     */
    private static Optional<User> getUser(HttpSession session) {
        return Optional.ofNullable((User) getAttribute(session, SESSION_KEY_USER));
    }

    /**
     * 세션에서 비밀번호 인증번호 조회
     * @param session
     * @return          비밀번호 인증번호
     */
    public static Optional<String> getPasswordCipher(HttpSession session) {
        return Optional.ofNullable((String) getAttribute(session, SESSION_KEY_PASSWORD_CIPHER));
    }

    /**
     * 세션에 정보 저장
     * @param session
     * @param key
     * @param value
     */
    public static void setAttribute(HttpSession session, String key, Object value) {
        session.setAttribute(key, value);
    }
    
    /**
     * 세션에 회원정보 등록
     * @param session
     * @param user      회원정보
     */
    private static void setUser(HttpSession session, User user) {
        setAttribute(session, SESSION_KEY_USER, user);
    }

    /**
     * 세션에 비밀번호 인증번호 등록
     * @param session
     * @param cipher    비밃번호 인증번호
     */
    public static void setPasswordCipher(HttpSession session, String cipher) {
        setAttribute(session, SESSION_KEY_PASSWORD_CIPHER, cipher);
    }
}
