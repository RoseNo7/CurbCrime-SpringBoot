package com.roseno.curbcrime.repository;

import com.roseno.curbcrime.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIdAndEmailAndIsDeletedFalse(String id, String email);               // 회원정보 조회 (비밀번호 찾기 시 사용)
    Optional<User> findByNameAndEmailAndIsDeletedFalse(String name, String email);           // 회원정보 조회 (아이디 찾기 시 사용)
    boolean existsById(String id);              // 아이디 사용 여부 확인
}
