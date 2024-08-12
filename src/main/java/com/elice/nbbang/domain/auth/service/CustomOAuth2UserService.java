package com.elice.nbbang.domain.auth.service;
import com.elice.nbbang.domain.auth.entity.Auth;
import com.elice.nbbang.domain.auth.repository.AuthRepository;
import com.elice.nbbang.domain.user.entity.UserRole;
import com.elice.nbbang.domain.user.repository.UserRepository;
import com.elice.nbbang.domain.auth.dto.CustomOAuth2User;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.global.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oauth2User = super.loadUser(userRequest);

        // 구글에서 사용자 정보 가져오기
        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        // Auth 정보 저장 또는 업데이트
        Auth auth = authRepository.findByProviderAndProviderId(Auth.Provider.GOOGLE, email)
                .orElseGet(() -> Auth.builder()
                        .provider(Auth.Provider.GOOGLE)
                        .providerId(email)
                        .build());

        // User 정보 저장 또는 업데이트
        User user = auth.getUser();
        if (user == null) {
            user = User.builder()
                    .email(email)
                    .nickname(name)
                    .role(UserRole.ROLE_USER)
                    .password("")  // 비밀번호는 필요 없음
                    .phoneNumber("") // 필요한 경우 설정
                    .build();
        } else {
            user.setNickname(name);
            // 다른 필요한 정보 업데이트 가능
        }
        userRepository.save(user);

        // Auth와 User의 연관관계 설정 및 저장
        auth.setUser(user);
        authRepository.save(auth);

        // JWT 토큰 생성
        String jwtToken = jwtUtil.createJwt("access", email, "ROLE_USER", 3600000L);

        return new CustomOAuth2User(oauth2User, jwtToken);
    }}