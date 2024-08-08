package com.elice.nbbang.domain.auth.controller;

import com.elice.nbbang.domain.auth.dto.CustomOAuth2User;
import com.elice.nbbang.global.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CookieValue;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse; // 패키지 변경
import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final JWTUtil jwtUtil;

    @GetMapping("/google/success")
    public ResponseEntity<Void> googleLoginSuccess(Authentication authentication, @CookieValue(name = "jwtToken", required = false) String jwtToken, HttpServletResponse response) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                System.out.println("Authentication is null or not authenticated");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            System.out.println("Authenticated user: " + customOAuth2User.getEmail());
            String newJwtToken = jwtUtil.createJwt("access", customOAuth2User.getEmail(), "ROLE_USER", 3600000L);

            // JWT 토큰을 쿠키에 설정
            Cookie jwtCookie = new Cookie("jwtToken", newJwtToken);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false); // 로컬 개발 환경에서는 false로 설정
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(3600); // 1시간
            response.addCookie(jwtCookie); // 쿠키를 응답에 추가

            // 쿠키가 잘 설정되었는지 확인하는 로그 추가
            System.out.println("Cookie Name: " + jwtCookie.getName());
            System.out.println("Cookie Value: " + jwtCookie.getValue());
            System.out.println("Cookie Path: " + jwtCookie.getPath());
            System.out.println("Cookie Max Age: " + jwtCookie.getMaxAge());

            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create("http://localhost:3000?googleLogin=success")); // 클라이언트 측으로 리디렉션


            return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}