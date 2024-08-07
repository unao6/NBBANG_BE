package com.elice.nbbang.domain.user.service;


import com.elice.nbbang.domain.auth.dto.OAuth2Response;
import com.elice.nbbang.domain.user.dto.UserResponse;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.entity.UserRole;
import com.elice.nbbang.domain.user.exception.UserNotFoundException;
import com.elice.nbbang.domain.user.repository.UserRepository;
import com.elice.nbbang.global.exception.ErrorCode;
import com.elice.nbbang.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserUtil userUtil;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // 헤더 표시 구분하려고 추가
    public boolean isAdmin(String email) {
        User user = userRepository.findByEmail(email);
        return user != null && user.getRole() == UserRole.ROLE_ADMIN;
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

    public UserResponse getUserInfo() {

        String email = userUtil.getAuthenticatedUserEmail();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException(ErrorCode.USER_NOT_FOUND);
        }
        boolean isAdmin = user.getRole() == UserRole.ROLE_ADMIN;

        return new UserResponse(user.getId(), user.getEmail(), user.getNickname(), user.getRole(), user.getPhoneNumber(), isAdmin);
    }
}