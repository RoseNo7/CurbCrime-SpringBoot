package com.roseno.curbcrime.repository;

import com.roseno.curbcrime.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long>, JpaSpecificationExecutor<Notice> {
    Optional<Notice> findByIdAndIsDeletedFalse(Long id);                            // 공지사항 조회
    boolean existsByIdAndIsDeletedFalse(Long id);                                   // 공지사항 여부

    @Modifying
    @Query("UPDATE Notice n SET n.viewCount = n.viewCount + 1 WHERE n.id = :id")
    void increaseViewCount(@Param("id") Long id);                                   // 조회수 증가
}
