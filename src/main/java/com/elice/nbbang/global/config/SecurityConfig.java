package com.elice.nbbang.global.config;

import com.elice.nbbang.domain.auth.repository.RefreshRepository;
import com.elice.nbbang.global.jwt.CustomLogoutFilter;
import com.elice.nbbang.global.jwt.JWTFilter;
import com.elice.nbbang.global.jwt.JWTUtil;
import com.elice.nbbang.global.jwt.LoginFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors
                        .configurationSource(request -> {
                            CorsConfiguration configuration = new CorsConfiguration();
                            configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                            configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                            configuration.setAllowCredentials(true);
                            configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
                            configuration.setMaxAge(3600L);
                            configuration.setExposedHeaders(Collections.singletonList("Authorization"));
                            return configuration;
                        }));

        http
                .csrf((csrf) -> csrf.disable())
                .formLogin((form) -> form.disable())
                .httpBasic((basic) -> basic.disable());

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/api/users/sign-up", "/login", "/", "/api/users/user-login", "/api/users/check-email", "/api/users/email-certification", "/oauth2/**", "/login/oauth2/**", "/api/auth/**").permitAll()
                        .requestMatchers("/api/users/**").hasRole("USER")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/reissue").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .anyRequest().authenticated());

        http.addFilterBefore(new JWTFilter(jwtUtil) {
            @Override
            protected boolean shouldNotFilter(HttpServletRequest request) {
                String path = request.getRequestURI();
                return path.startsWith("/oauth2/authorization") || path.startsWith("/login/oauth2/code") || path.startsWith("/api/auth");
            }
        }, UsernamePasswordAuthenticationFilter.class);

        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshRepository), UsernamePasswordAuthenticationFilter.class);
        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);

        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}