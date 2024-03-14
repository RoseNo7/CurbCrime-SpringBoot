package com.roseno.curbcrime.mapper;

import com.roseno.curbcrime.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {
    Optional<User> findUserByIdAndPassword(String id, String password);         // 회원정보 조회 (로그인 시 사용)
}
