package com.roseno.curbcrime.specification;

import com.roseno.curbcrime.domain.Notice;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

/**
 * 검색 옵션
 */
public class NoticeSpecification {
                                                                                    // 검색 옵션
    public static final String SEARCH_OPTION_TITLE = "TITLE";                       // 제목
    public static final String SEARCH_OPTION_CONTENT = "CONTENT";                   // 내용
    public static final String SEARCH_OPTION_TITLE_OR_CONTENT = "TITLE_CONTENT";    // 제목 + 내용

    public static Specification<Notice> isNotDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("isDeleted"));
    }

    public static Specification<Notice> titleContains(String keyword) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + keyword + "%");
    }

    public static Specification<Notice> contentContains(String keyword) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("content")), "%" + keyword + "%");
    }

    public static Specification<Notice> titleOrContentContains(String keyword) {
        return Specification.where(titleContains(keyword)).or(contentContains(keyword));
    }

    public static Specification<Notice> bySearchOption(String searchOption, String keyword) {
        return switch (Optional.ofNullable(searchOption).orElse("")) {
            case SEARCH_OPTION_TITLE -> titleContains(Optional.ofNullable(keyword).orElse("")).and(isNotDeleted());
            case SEARCH_OPTION_CONTENT -> contentContains(Optional.ofNullable(keyword).orElse("")).and(isNotDeleted());
            case SEARCH_OPTION_TITLE_OR_CONTENT -> titleOrContentContains(Optional.ofNullable(keyword).orElse("")).and(isNotDeleted());
            default -> isNotDeleted();
        };
    }
}
