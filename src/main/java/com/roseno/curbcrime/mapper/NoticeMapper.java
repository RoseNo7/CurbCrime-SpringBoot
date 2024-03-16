package com.roseno.curbcrime.mapper;

import com.roseno.curbcrime.domain.Notice;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface NoticeMapper {
    int findNoticeCount(String searchOption, String keyword);                                                   // 공지사항 목록 개수 조회
    List<Notice> findNotices(int offset, int limit, String sortOption, String searchOption, String keyword);    // 공지사항 목록 조회
    Optional<Notice> findNoticeById(long id);                                                                   // 공지사항 조회
}
