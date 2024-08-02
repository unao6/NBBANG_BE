package com.elice.nbbang.domain.auth.controller;

import com.elice.nbbang.domain.auth.dto.AuthResponse;
import com.elice.nbbang.domain.auth.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final DefaultOAuth2UserService defaultOAuth2UserService;
    private final OAuthService oAuthService;

    @GetMapping("/google")
    public ResponseEntity<Void> googleLogin() {
        String googleLoginUrl = "https://accounts.google.com/o/oauth2/auth?client_id=YOUR_CLIENT_ID&redirect_uri=http://localhost:8080/api/auth/google/callback&response_type=code&scope=email%20profile";
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(googleLoginUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/google/callback")
    public ResponseEntity<?> googleCallback(@RequestParam("code") String code) {
        try {
            OAuth2AccessTokenResponse accessTokenResponse = requestAccessToken(code);

            ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("google");
            OAuth2UserRequest userRequest = new OAuth2UserRequest(clientRegistration, accessTokenResponse.getAccessToken());
            OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);

            AuthResponse authResponse = oAuthService.handleGoogleLogin(oAuth2User);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authResponse.getAccessToken());
            headers.set("Refresh-Token", authResponse.getRefreshToken());
            headers.setLocation(URI.create("http://localhost:3000"));

            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } catch (Exception e) {
            log.error("구글 인증 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("구글 인증 실패");
        }
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