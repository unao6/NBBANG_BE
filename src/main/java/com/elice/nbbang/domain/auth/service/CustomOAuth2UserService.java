package com.elice.nbbang.domain.auth.service;

import com.elice.nbbang.domain.auth.dto.CustomOAuth2User;
import com.elice.nbbang.domain.auth.dto.GoogleResponse;
import com.elice.nbbang.domain.auth.dto.OAuth2Response;
import com.elice.nbbang.domain.user.dto.UserSignUpDto;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.entity.UserRole;
import com.elice.nbbang.domain.user.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        UserSignUpDto userSignUpDto = new UserSignUpDto(
                oAuth2Response.getEmail(),
                "", // 비밀번호는 OAuth2 인증 시에는 제공되지 않으므로 빈 문자열로 설정합니다.
                oAuth2Response.getName(),
                "" // 전화번호는 OAuth2 인증 시에는 제공되지 않으므로 빈 문자열로 설정합니다.
        );

        User existingUser = userRepository.findByEmail(userSignUpDto.getEmail());

        if (existingUser == null) {
            User newUser = User.builder()
                    .email(userSignUpDto.getEmail())
                    .nickname(userSignUpDto.getNickname())
                    .password(userSignUpDto.getPassword())
//                    .phoneNumber(userSignUpDto.getPhoneNumber())
                    .role(UserRole.ROLE_USER) // 기본 역할 설정
                    .build();

            userRepository.save(newUser);
        } else {
            existingUser.setNickname(userSignUpDto.getNickname());
            userRepository.save(existingUser);
        }

        return new CustomOAuth2User(userSignUpDto);
    }
}