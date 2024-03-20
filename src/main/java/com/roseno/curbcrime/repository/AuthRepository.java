package com.roseno.curbcrime.repository;

import com.roseno.curbcrime.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<User, Long> {
    User findById(String id);            // 회원정보 조회
}
