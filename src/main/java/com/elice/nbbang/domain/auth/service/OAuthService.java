package com.elice.nbbang.domain.auth.service;

import com.elice.nbbang.domain.auth.dto.AuthResponse;
import com.elice.nbbang.domain.auth.dto.GoogleResponse;
import com.elice.nbbang.domain.auth.dto.OAuth2Response;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.entity.UserRole;
import com.elice.nbbang.domain.user.repository.UserRepository;
import com.elice.nbbang.global.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final CustomOAuth2UserService customOAuth2UserService;

    public AuthResponse handleGoogleLogin(OAuth2User oAuth2User) {
        OAuth2Response oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());

        User existingUser = userRepository.findByEmail(oAuth2Response.getEmail());
        if (existingUser == null) {
            User newUser = User.builder()
                    .email(oAuth2Response.getEmail())
                    .nickname(oAuth2Response.getName())
                    .role(UserRole.ROLE_USER)
                    .build();
            userRepository.save(newUser);
            existingUser = newUser;
        } else {
            existingUser.setNickname(oAuth2Response.getName());
            userRepository.save(existingUser);
        }

        String jwtAccessToken = jwtUtil.createJwt("access", existingUser.getEmail(), "ROLE_USER", 3600000L);
        String jwtRefreshToken = jwtUtil.createJwt("refresh", existingUser.getEmail(), "ROLE_REFRESH", 604800000L);

        return new AuthResponse(jwtAccessToken, jwtRefreshToken);
    }
}