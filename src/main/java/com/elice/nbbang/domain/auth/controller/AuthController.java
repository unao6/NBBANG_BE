package com.elice.nbbang.domain.auth.controller;

import com.elice.nbbang.domain.auth.dto.AuthResponse;
import com.elice.nbbang.domain.auth.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final DefaultOAuth2UserService defaultOAuth2UserService;
    private final OAuthService oAuthService;

    @GetMapping("/google")
    public void googleLogin(HttpServletResponse response) {
        String googleLoginUrl = "https://accounts.google.com/o/oauth2/auth?client_id=YOUR_CLIENT_ID&redirect_uri=http://localhost:8080/api/auth/google/callback&response_type=code&scope=email profile";
        response.setHeader("Location", googleLoginUrl);
        response.setStatus(302);
    }

    @GetMapping("/google/callback")
    public ResponseEntity<?> googleCallback(@RequestParam("code") String code, HttpServletResponse response) {
        // 구글 토큰 엔드포인트에 요청하여 액세스 토큰을 받음
        OAuth2AccessTokenResponse accessTokenResponse = requestAccessToken(code);

        // 사용자 정보를 가져오기 위한 OAuth2UserRequest 생성
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("google");
        OAuth2UserRequest userRequest = new OAuth2UserRequest(clientRegistration, accessTokenResponse.getAccessToken());
        OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);

        // OAuthService를 사용하여 JWT 토큰 발급
        AuthResponse authResponse = oAuthService.handleGoogleLogin(code);

        // JWT 토큰을 응답 헤더에 설정
        response.setHeader("Authorization", "Bearer " + authResponse.getAccessToken());
        response.setHeader("Refresh-Token", authResponse.getRefreshToken());

        // 클라이언트로 리다이렉트
        response.setHeader("Location", "http://localhost:3000");
        response.setStatus(302);

        return ResponseEntity.ok().build();
    }

    private OAuth2AccessTokenResponse requestAccessToken(String code) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("google");
        String tokenUri = clientRegistration.getProviderDetails().getTokenUri();

        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", clientRegistration.getClientId());
        params.put("client_secret", clientRegistration.getClientSecret());
        params.put("redirect_uri", clientRegistration.getRedirectUri());
        params.put("grant_type", "authorization_code");

        return restTemplate.postForObject(tokenUri, params, OAuth2AccessTokenResponse.class);
    }
}