package com.roseno.curbcrime.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roseno.curbcrime.dto.api.ApiErrorResponse;
import com.roseno.curbcrime.dto.api.ApiResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class UnauthorizedEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ApiErrorResponse apiResponse = ApiErrorResponse.builder()
                .code(HttpServletResponse.SC_UNAUTHORIZED)
                .status(ApiResult.ERROR.status())
                .message("인증되지 않은 사용자입니다.")
                .build();

        String responseBody = objectMapper.writeValueAsString(apiResponse);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(responseBody);
    }
}
