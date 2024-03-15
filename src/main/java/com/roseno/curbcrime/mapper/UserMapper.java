package com.roseno.curbcrime.mapper;

import com.roseno.curbcrime.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {
    Optional<User> findUserByIdAndPassword(String id, String password);         // 회원정보 조회 (로그인 시 사용)
    Optional<User> findIdByNameAndEmail(String name, String email);             // 아이디 찾기
    boolean isUsedIdx(long idx);                                                // 회원번호 사용 여부 확인
    boolean isUsedId(String id);                                                // 아이디 사용 여부 확인
    int createUser(User user);              // 회원가입
}
