package com.roseno.curbcrime.controller;

import com.roseno.curbcrime.dto.api.ApiResponse;
import com.roseno.curbcrime.dto.api.ApiResult;
import com.roseno.curbcrime.dto.notice.NoticeRequest;
import com.roseno.curbcrime.dto.notice.NoticeResponse;
import com.roseno.curbcrime.dto.notice.PageResponse;
import com.roseno.curbcrime.service.NoticeService;
import com.roseno.curbcrime.util.SessionUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
        NoticeResponse noticeResponse = noticeService.findNotice(id);

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .status(ApiResult.SUCCESS.status())
                .data(noticeResponse)
                .build();

        return ResponseEntity.ok().body(apiResponse);
    }

    /**
     * 공지사항 생성
     * @param noticeRequest     공지사항
     * @return
     */
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(method = RequestMethod.POST, value = "")
    public ResponseEntity<ApiResponse<Object>> addNotice(@RequestBody @Valid NoticeRequest noticeRequest) {
         Long idx = SessionUtil.getCurrentUserIdx().orElse(-1L);
         
         Optional<NoticeResponse> optNotice = noticeService.createNotice(idx, noticeRequest);

         if (optNotice.isPresent()) {
             NoticeResponse noticeResponse = optNotice.get();
             long noticeId = noticeResponse.getId();

             URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                     .path("/{id}")
                     .buildAndExpand(noticeId)
                     .toUri();

             ApiResponse<Object> apiResponse = ApiResponse.builder()
                     .code(HttpStatus.CREATED.value())
                     .status(ApiResult.SUCCESS.status())
                     .message("공지사항이 생성되었습니다.")
                     .build();

             return ResponseEntity.created(location).body(apiResponse);
         } else {
             ApiResponse<Object> apiResponse = ApiResponse.builder()
                     .code(HttpStatus.OK.value())
                     .status(ApiResult.FAILED.status())
                     .message("공지사항 생성에 실패하였습니다.")
                     .build();

             return ResponseEntity.ok(apiResponse);
         }
    }

    /**
     * 공지사항 수정
     * @param id                공지사항 아이디
     * @param noticeRequest     공지사항
     * @return
     */
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(method = RequestMethod.PATCH, value = "/{id}")
    public ResponseEntity<ApiResponse<Object>> modifyNotice(@PathVariable long id,
                                                            @RequestBody @Valid NoticeRequest noticeRequest) {
        noticeService.updateNotice(id, noticeRequest);

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .status(ApiResult.SUCCESS.status())
                .message("공지사항이 변경되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 공지사항 삭제
     * @param id        공지사항 아이디
     * @return
     */
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResponseEntity<ApiResponse<Object>> removeNotice(@PathVariable long id) {
        noticeService.deleteNotice(id);

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .status(ApiResult.SUCCESS.status())
                .message("공지사항이 삭제되었습니다.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * 공지사항 조회수 증가
     * @param id        공지사항 아이디
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/views")
    public ResponseEntity<ApiResponse<Object>> incrementNoticeViews(@PathVariable long id) {
        noticeService.incrementNoticeView(id);

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .status(ApiResult.SUCCESS.status())
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}
