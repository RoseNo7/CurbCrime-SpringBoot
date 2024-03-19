package com.roseno.curbcrime.mapper;

import com.roseno.curbcrime.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthMapper {
    User findUserById(String id);           // 회원정보 조회
}
