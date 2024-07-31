package com.elice.nbbang.domain.auth.dto;

import com.elice.nbbang.domain.user.dto.UserSignUpDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final UserSignUpDto userSignUpDto;

    public CustomOAuth2User(UserSignUpDto userSignUpDto) {
        this.userSignUpDto = userSignUpDto;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return "ROLE_USER"; // 기본 역할을 "ROLE_USER"로 설정합니다.
            }
        });

        return collection;
    }

    @Override
    public String getName() {
        return userSignUpDto.getNickname(); // 닉네임을 이름으로 사용합니다.
    }

    public String getEmail() {
        return userSignUpDto.getEmail();
    }

    public String getNickname() {
        return userSignUpDto.getNickname();
    }
}
