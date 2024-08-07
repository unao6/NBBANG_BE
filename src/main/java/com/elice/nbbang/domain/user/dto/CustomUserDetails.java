package com.elice.nbbang.domain.user.dto;

import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(UserSignUpDto userSignUpDto) {
        this.user = new User();
        this.user.setEmail(userSignUpDto.getEmail());
        this.user.setPassword(userSignUpDto.getPassword());
        this.user.setNickname(userSignUpDto.getNickname());

        // PhoneCerfiticationRequestDto 객체로부터 phoneNumber를 추출
        PhoneCerfiticationRequestDto phoneCerfiticationRequestDto = userSignUpDto.getPhoneCerfiticationRequestDto();
        if (phoneCerfiticationRequestDto != null) {
            this.user.setPhoneNumber(phoneCerfiticationRequestDto.getPhoneNumber());
        } else {
            this.user.setPhoneNumber(null); // phoneCerfiticationRequestDto가 없을 경우 null로 설정
        }

        this.user.setRole(UserRole.ROLE_USER); // 기본 역할 설정
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new SimpleGrantedAuthority(user.getRole().name()));
        return collection;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}