package com.roseno.curbcrime.controller;

import com.roseno.curbcrime.dto.api.ApiResponse;
import com.roseno.curbcrime.dto.api.ApiResult;
import com.roseno.curbcrime.dto.notice.NoticeResponse;
import com.roseno.curbcrime.dto.notice.PageResponse;
import com.roseno.curbcrime.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 공지사항 목록 조회
     * @param page              페이지 번호
     * @param count             목록 개수
     * @param sortOption        정렬 옵션
     * @param searchOption      검색 옵션
     * @param keyword           검색어
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "")
    public ResponseEntity<ApiResponse<Object>> getNotices(@RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "10") int count,
                                                          @RequestParam(name = "sort_option", defaultValue = NoticeService.SORT_OPTION_LATEST) String sortOption,
                                                          @RequestParam(name = "search_option", required = false) String searchOption,
                                                          @RequestParam(required = false) String keyword) {
        PageResponse pageResponse = noticeService.findNotices(page, count, sortOption, searchOption, keyword);

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .status(ApiResult.SUCCESS.status())
                .data(pageResponse)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 공지사항 조회
     * @param id    공지사항 아이디
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<ApiResponse<Object>> getNotice(@PathVariable long id) {
        Optional<NoticeResponse> optNotice = noticeService.findNotice(id);

        if (optNotice.isPresent()) {
            NoticeResponse noticeResponse = optNotice.get();

            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.SUCCESS.status())
                    .data(noticeResponse)
                    .build();

            return ResponseEntity.ok().body(apiResponse);
        } else {
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .status(ApiResult.FAILED.status())
                    .message("공지사항을 조회할 수 없습니다.")
                    .build();

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
    }
}
