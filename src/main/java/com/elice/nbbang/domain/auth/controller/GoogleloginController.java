package com.elice.nbbang.domain.auth.controller;

import com.elice.nbbang.domain.auth.dto.GoogleResponse;
import com.elice.nbbang.domain.auth.dto.GoogleUserInfo;
import com.elice.nbbang.domain.auth.dto.OAuth2Response;
import com.elice.nbbang.domain.auth.service.GoogleService;
import com.elice.nbbang.domain.auth.service.JwtTokenProvider;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class GoogleloginController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final GoogleService googleService;

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> body) {
        String tokenId = body.get("token");

        // 구글 API를 사용하여 tokenId 검증 및 사용자 정보 획득
        GoogleUserInfo googleUserInfo = googleService.verifyToken(tokenId);

        if (googleUserInfo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        // 사용자 정보로 OAuth2Response 생성
        OAuth2Response oAuth2Response = new GoogleResponse(Map.of(
                "sub", googleUserInfo.getSub(),
                "email", googleUserInfo.getEmail(),
                "name", googleUserInfo.getName(),
                "picture", googleUserInfo.getPicture()
        ));

        // 사용자 정보로 User 엔티티 생성 또는 기존 사용자 찾기
        User user = userService.findOrCreateUser(oAuth2Response);

        // JWT 토큰 생성 및 반환
        String jwtToken = jwtTokenProvider.createToken(user.getEmail(), user.getRole().name());

        return ResponseEntity.ok().body(Map.of("token", jwtToken));
    }
}