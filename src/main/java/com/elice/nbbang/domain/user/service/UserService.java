package com.elice.nbbang.domain.user.service;

import com.elice.nbbang.domain.user.dto.UserLogInDto;
import com.elice.nbbang.domain.user.dto.UserSignUpDto;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    //로그인 이메일 중복 검사 메서드
    public boolean checkLoginDuplicate(String email) {
        return userRepository.existsByLoginEmail(email);
    }

    //회원가입 메서드
    public void signUp(UserSignUpDto userSignUpDto) {
        userRepository.save(UserSignUpDto.toEntity());
    }

    //로그인 메서드
    public User login(UserLogInDto userLogInDto) {
        User findUser = userRepository.findByLoginEmail(UserLogInDto.getEmail());

        if(findUser == null) {
            return null;
        }

        if (!findUser.getPassword().equals(userLogInDto.getPassword())) {
            return null;
        }

        return findUser;
    }

    //로그인한 User 반환 메서드
    public User getLoginUserByEmail(String email) {
        if (email == null) return null;

        Optional<User> findUser = userRepository.findByLoginEmail(email);
        return findUser.orElse(null);
    }
}