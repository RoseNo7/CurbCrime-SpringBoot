package com.roseno.curbcrime.controller;

import com.roseno.curbcrime.dto.api.ApiResponse;
import com.roseno.curbcrime.dto.api.ApiResult;
import com.roseno.curbcrime.dto.notice.NoticeRequest;
import com.roseno.curbcrime.dto.notice.NoticeResponse;
import com.roseno.curbcrime.dto.notice.PageResponse;
import com.roseno.curbcrime.model.Role;
import com.roseno.curbcrime.service.NoticeService;
import com.roseno.curbcrime.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    /**
     * 공지사항 생성
     * @param session
     * @param noticeRequest     공지사항
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "")
    public ResponseEntity<ApiResponse<Object>> addNotice(HttpSession session,
                                                         @RequestBody @Valid NoticeRequest noticeRequest) {
         Optional<Long> optIdx = SessionUtil.getCurrentUserIdx(session);

         if (optIdx.isEmpty()) {
             ApiResponse<Object> apiResponse = ApiResponse.builder()
                     .code(HttpStatus.UNAUTHORIZED.value())
                     .status(ApiResult.ERROR.status())
                     .message("인증되지 않은 사용자입니다.")
                     .build();

             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
         }

         Optional<String> optRole = SessionUtil.getCurrentUserRole(session);

         if (optRole.isPresent()) {
            String role = optRole.get();

            if (!Role.ADMIN.id().equals(role)) {
                ApiResponse<Object> apiResponse = ApiResponse.builder()
                        .code(HttpStatus.FORBIDDEN.value())
                        .status(ApiResult.ERROR.status())
                        .message("요청된 작업에 필요한 권한이 없습니다.")
                        .build();

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
            }
         }

         long idx = optIdx.get();
         Optional<Long> optNoticeId = noticeService.createNotice(idx, noticeRequest);

         if (optNoticeId.isPresent()) {
             long noticeId = optNoticeId.get();

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
     * @param session
     * @param id                공지사항 아이디
     * @param noticeRequest     공지사항
     * @return
     */
    @RequestMapping(method = RequestMethod.PATCH, value = "/{id}")
    public ResponseEntity<ApiResponse<Object>> modifyNotice(HttpSession session,
                                                            @PathVariable long id,
                                                            @RequestBody @Valid NoticeRequest noticeRequest) {
        Optional<Long> optIdx = SessionUtil.getCurrentUserIdx(session);

        if (optIdx.isEmpty()) {
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.UNAUTHORIZED.value())
                    .status(ApiResult.ERROR.status())
                    .message("인증되지 않은 사용자입니다.")
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        }

        Optional<String> optRole = SessionUtil.getCurrentUserRole(session);

        if (optRole.isPresent()) {
            String role = optRole.get();

            if (!Role.ADMIN.id().equals(role)) {
                ApiResponse<Object> apiResponse = ApiResponse.builder()
                        .code(HttpStatus.FORBIDDEN.value())
                        .status(ApiResult.ERROR.status())
                        .message("요청된 작업에 필요한 권한이 없습니다.")
                        .build();

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
            }
        }

        boolean isUpdated = noticeService.updateNotice(id, noticeRequest);

        if (isUpdated) {
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.SUCCESS.status())
                    .message("공지사항이 변경되었습니다.")
                    .build();

            return ResponseEntity.ok(apiResponse);
        } else {
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .status(ApiResult.FAILED.status())
                    .message("공지사항을 찾을 수 없습니다.")
                    .build();

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
    }

    /**
     * 공지사항 삭제
     * @param session
     * @param id        공지사항 아이디
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResponseEntity<ApiResponse<Object>> removeNotice(HttpSession session,
                                                            @PathVariable long id) {
        Optional<Long> optIdx = SessionUtil.getCurrentUserIdx(session);

        if (optIdx.isEmpty()) {
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.UNAUTHORIZED.value())
                    .status(ApiResult.ERROR.status())
                    .message("인증되지 않은 사용자입니다.")
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        }

        Optional<String> optRole = SessionUtil.getCurrentUserRole(session);

        if (optRole.isPresent()) {
            String role = optRole.get();

            if (!Role.ADMIN.id().equals(role)) {
                ApiResponse<Object> apiResponse = ApiResponse.builder()
                        .code(HttpStatus.FORBIDDEN.value())
                        .status(ApiResult.ERROR.status())
                        .message("요청된 작업에 필요한 권한이 없습니다.")
                        .build();

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
            }
        }

        boolean isDeleted = noticeService.deleteNotice(id);

        if (isDeleted) {
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.SUCCESS.status())
                    .message("공지사항이 삭제되었습니다.")
                    .build();

            return ResponseEntity.ok(apiResponse);
        } else {
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .status(ApiResult.FAILED.status())
                    .message("공지사항을 찾을 수 없습니다.")
                    .build();

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
    }

    /**
     * 공지사항 조회수 증가
     * @param id        공지사항 아이디
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/views")
    public ResponseEntity<ApiResponse<Object>> incrementNoticeViews(@PathVariable long id) {
        boolean isIncreased = noticeService.incrementNoticeView(id);

        if (isIncreased) {
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .status(ApiResult.SUCCESS.status())
                    .build();

            return ResponseEntity.ok(apiResponse);
        } else {
            ApiResponse<Object> apiResponse = ApiResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .status(ApiResult.FAILED.status())
                    .message("공지사항을 찾을 수 없습니다.")
                    .build();

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
    }
}
