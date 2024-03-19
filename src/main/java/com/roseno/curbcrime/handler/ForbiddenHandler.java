package com.roseno.curbcrime.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roseno.curbcrime.dto.api.ApiErrorResponse;
import com.roseno.curbcrime.dto.api.ApiResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class ForbiddenHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ApiErrorResponse apiResponse = ApiErrorResponse.builder()
                .code(HttpServletResponse.SC_FORBIDDEN)
                .status(ApiResult.ERROR.status())
                .message("요청된 작업에 필요한 권한이 없습니다.")
                .build();

        String responseBody = objectMapper.writeValueAsString(apiResponse);

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(responseBody);
    }
}
