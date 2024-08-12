package com.elice.nbbang.domain.auth.controller;

import com.elice.nbbang.domain.auth.dto.CustomOAuth2User;
import com.elice.nbbang.global.jwt.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.struts.chain.commands.UnauthorizedActionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JWTUtil jwtUtil;

    @GetMapping("/google/success")
    public void googleLoginSuccess(HttpServletResponse response, Authentication authentication) throws IOException {
        if (authentication == null || !authentication.isAuthenticated()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        // 사용자 정보 추출
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = customOAuth2User.getEmail();

        // JWT 토큰 생성
        String jwtToken = jwtUtil.createJwt("access", email, "ROLE_USER", 3600000L);

        // 쿠키에 토큰 추가
        Cookie jwtCookie = new Cookie("access_token", jwtToken);
        jwtCookie.setHttpOnly(true); // HttpOnly 쿠키 설정
        jwtCookie.setSecure(false); // 개발 중이라면 false, 배포 시 true
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(3600); // 쿠키 만료 시간 설정
        response.addCookie(jwtCookie);

        // 클라이언트로 리다이렉트
        response.sendRedirect("http://localhost:3000/redirect");
    }

    @GetMapping("/token")
    public ResponseEntity<String> getAccessToken(@CookieValue(name = "access_token", required = false) String accessToken) {
        log.info("넘어온 토큰: {}", accessToken);

        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No token found");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("access", accessToken);

        // 로그에 토큰 출력
        log.info("넘어온 토큰22222: {}", accessToken);
        return ResponseEntity.ok().headers(headers).body("Token sent in headers");
    }

}