package com.roseno.curbcrime.util;

import com.roseno.curbcrime.domain.User;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;

public class SessionUtil {

    private static final String SESSION_KEY_USER = "USER";

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
     * 세션에서 회원정보 조회
     * @param session
     * @return          회원정보
     */
    private static Optional<User> getUser(HttpSession session) {
        return Optional.ofNullable((User) session.getAttribute(SESSION_KEY_USER));
    }

    /**
     * 세션에 회원정보 등록
     * @param session
     * @param user      회원정보
     */
    private static void setUser(HttpSession session, User user) {
        session.setAttribute(SESSION_KEY_USER, user);
    }
}
