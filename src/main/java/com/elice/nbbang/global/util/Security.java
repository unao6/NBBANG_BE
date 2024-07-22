package com.elice.nbbang.global.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class Security {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/h2-console/**").permitAll()  // H2 콘솔 접근 허용
                    .anyRequest().permitAll()  // 모든 요청에 대해 인증 요구 없음
            )
            .csrf(csrf -> csrf.disable())  // CSRF 보호 비활성화
            .headers(headers -> headers.frameOptions().disable());  // H2 콘솔 사용을 위해 X-Frame-Options 비활성화
        return http.build();
    }
}
