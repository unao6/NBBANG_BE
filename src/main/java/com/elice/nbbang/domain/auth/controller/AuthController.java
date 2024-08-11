package com.elice.nbbang.domain.auth.controller;

import com.elice.nbbang.domain.auth.dto.CustomOAuth2User;
import com.elice.nbbang.domain.auth.dto.request.AddPhoneNumberRequest;
import com.elice.nbbang.domain.user.dto.request.ChangePhoneNumberRequest;
import com.elice.nbbang.domain.user.service.UserService;
import com.elice.nbbang.global.jwt.JWTUtil;
import com.elice.nbbang.global.util.UserUtil;
import com.elice.nbbang.domain.auth.repository.RefreshRepository;
import com.elice.nbbang.domain.auth.entity.RefreshEntity;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JWTUtil jwtUtil;
    private final UserService userService;
    private final UserUtil userUtil;
    private final RefreshRepository refreshRepository;

    private static final long ACCESS_TOKEN_EXPIRATION_MS = 3600000L; // 1 hour
    private static final long REFRESH_TOKEN_EXPIRATION_MS = 86400000L; // 24 hours

    @GetMapping("/google/success")
    public void googleLoginSuccess(HttpServletResponse response, Authentication authentication) throws IOException {
        if (authentication == null || !authentication.isAuthenticated()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        // 사용자 정보 추출
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = customOAuth2User.getEmail();

        // Access 토큰 생성
        String accessToken = jwtUtil.createJwt("access", email, "ROLE_USER", ACCESS_TOKEN_EXPIRATION_MS);

        // Refresh 토큰 생성 및 저장
        String refreshToken = jwtUtil.createJwt("refresh", email, "ROLE_USER", REFRESH_TOKEN_EXPIRATION_MS);
        addRefreshEntity(email, refreshToken, REFRESH_TOKEN_EXPIRATION_MS);

        // 쿠키에 Access 토큰 추가 (일반 로그인과 동일한 쿠키 이름 사용)
        Cookie accessCookie = new Cookie("access", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(false); // 개발 중이라면 false, 배포 시 true
        accessCookie.setPath("/");
        accessCookie.setMaxAge((int) (ACCESS_TOKEN_EXPIRATION_MS / 1000));
        response.addCookie(accessCookie);

        // 쿠키에 Refresh 토큰 추가 (일반 로그인과 동일한 쿠키 이름 사용)
        Cookie refreshCookie = new Cookie("refresh", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false); // 개발 중이라면 false, 배포 시 true
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge((int) (REFRESH_TOKEN_EXPIRATION_MS / 1000));
        response.addCookie(refreshCookie);

        // 클라이언트로 리다이렉트
        response.sendRedirect("http://localhost:3000/redirect");
    }

    @GetMapping("/token")
    public ResponseEntity<String> getAccessToken(@CookieValue(name = "access", required = false) String accessToken, HttpServletResponse response) {
        log.info("넘어온 토큰: {}", accessToken);

        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No token found");
        }

        // 헤더에 토큰 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add("access", accessToken);

        // 쿠키에서 access 토큰 제거
        Cookie deleteAccessCookie = new Cookie("access", null);
        deleteAccessCookie.setMaxAge(0);
        deleteAccessCookie.setPath("/");
        response.addCookie(deleteAccessCookie);

        // 로그에 토큰 출력
        log.info("넘어온 토큰22222: {}", accessToken);
        return ResponseEntity.ok().headers(headers).body("Token sent in headers");
    }

    // 휴대폰 번호 추가
    @PostMapping("/add-phone-number")
    public ResponseEntity<String> addPhoneNumber(@RequestBody AddPhoneNumberRequest request) {
        String email = userUtil.getAuthenticatedUserEmail();
        System.out.println("Authenticated user email: " + email);
        System.out.println("New phone number: " + request.getPhoneNumber());
        System.out.println("Verification code: " + request.getRandomNumber());

        boolean isAdded = userService.addPhoneNumberAfterSocialLogin(email, request.getPhoneNumber(), request.getRandomNumber());
        if (isAdded) {
            return ResponseEntity.ok("휴대폰 번호가 성공적으로 추가되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("휴대폰 번호 추가에 실패했습니다.");
        }
    }

    private void addRefreshEntity(String email, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = RefreshEntity.builder()
                .email(email)
                .refresh(refresh)
                .expiration(date.toString())
                .build();

        refreshRepository.save(refreshEntity);
    }
}