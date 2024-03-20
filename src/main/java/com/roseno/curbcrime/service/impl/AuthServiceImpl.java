package com.roseno.curbcrime.service.impl;

import com.roseno.curbcrime.domain.User;
import com.roseno.curbcrime.dto.auth.AuthRequest;
import com.roseno.curbcrime.model.Principal;
import com.roseno.curbcrime.repository.AuthRepository;
import com.roseno.curbcrime.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService, UserDetailsService {
    private final AuthRepository authRepository;
    private final AuthenticationManagerBuilder managerBuilder;
    private final SecurityContextRepository contextRepository;

    /**
     * 로그인
     * @param request
     * @param response
     * @param authRequest   회원정보
     */
    @Override
    public void authenticate(HttpServletRequest request,
                             HttpServletResponse response,
                             AuthRequest authRequest) {
        String id = authRequest.getId();
        String password = authRequest.getPassword();

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(id, password);
        
        // loadUserByUsername() 호출
        Authentication authentication = managerBuilder.getObject().authenticate(token);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
        contextRepository.saveContext(securityContext, request, response);
    }

    /**
     * Security 사용자 인증
     * @param username      회원 아이디
     * @return              회원정보
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = authRepository.findById(username);

        if (user == null) {
            throw new BadCredentialsException("");
        }

        return Principal.builder()
                .idx(user.getIdx())
                .id(user.getId())
                .password(user.getPassword())
                .roles(Collections.singletonList((GrantedAuthority) user::getRole))
                .build();
    }
}
