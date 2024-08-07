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

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final JWTUtil jwtUtil;

    @GetMapping("/google/success")
    public ResponseEntity<Void> googleLoginSuccess(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String jwtToken = jwtUtil.createJwt("access", customOAuth2User.getEmail(), "ROLE_USER", 3600000L);

        HttpHeaders headers = new HttpHeaders();
        headers.set("access",  jwtToken);
        headers.setLocation(URI.create("http://localhost:3000")); // 클라이언트 측으로 리디렉션

        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
    }
}