package com.elice.nbbang.domain.user.service;


import com.elice.nbbang.domain.auth.dto.OAuth2Response;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.entity.UserRole;
import com.elice.nbbang.domain.user.repository.UserRepository;
import com.elice.nbbang.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserUtil userUtil;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean isEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    // 회원 탈퇴
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email);

        user.setDeleted(true);
        userRepository.save(user);
    }

    // 탈퇴 안한 회원만 조회
    public List<User> getAllActiveUsers() {
        return userRepository.findAllByDeletedFalse();
    }

    // 탈퇴한 회원 복구
    public void restoreUser(String email) {
        User user = userRepository.findByEmail(email);

        user.setDeleted(false);
        userRepository.save(user);
    }

    public User findOrCreateUser(OAuth2Response oAuth2Response) {
        User user = userRepository.findByEmail(oAuth2Response.getEmail());
        if (user == null) {
            User newUser = new User();
            newUser.setEmail(oAuth2Response.getEmail());
            newUser.setNickname(oAuth2Response.getName());
            newUser.setRole(UserRole.ROLE_USER);
            user = userRepository.save(newUser);
        }
        return user;
    }
}