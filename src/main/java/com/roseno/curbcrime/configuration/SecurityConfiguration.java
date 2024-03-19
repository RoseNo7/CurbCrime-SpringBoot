package com.roseno.curbcrime.configuration;

import com.roseno.curbcrime.handler.ForbiddenHandler;
import com.roseno.curbcrime.handler.UnauthorizedEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final UnauthorizedEntryPoint unauthorizedEntryPoint;
    private final ForbiddenHandler forbiddenHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry -> registry
                        .requestMatchers("/api/v1/my-account/**", "/api/v1/auth/logout").authenticated()
                        .requestMatchers("/api/v1/accounts/**", "/api/v1/notices/**", "/api/v1/auth/login").permitAll()
                        .anyRequest().denyAll()
                )
                .securityContext(securityContext ->securityContext
                    .securityContextRepository(securityContextRepository())
                    .requireExplicitSave(true)
                )
                .exceptionHandling(configurer -> configurer
                        .accessDeniedHandler(forbiddenHandler)
                        .authenticationEntryPoint(unauthorizedEntryPoint)
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Spring Context Repository Bean 설정
     * @return
     */
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository()
        );
    }
}
