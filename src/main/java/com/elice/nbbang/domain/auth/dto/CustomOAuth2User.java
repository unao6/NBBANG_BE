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
        return null; // 필요에 따라 구현
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add((GrantedAuthority) () -> "ROLE_USER");
        return collection;
    }

    @Override
    public String getName() {
        return userSignUpDto.getNickname();
    }

    public String getEmail() {
        return userSignUpDto.getEmail();
    }
}