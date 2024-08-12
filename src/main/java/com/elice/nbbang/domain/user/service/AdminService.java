package com.elice.nbbang.domain.user.service;

import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.entity.UserRole;
import com.elice.nbbang.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    // 사용자를 관리자 역할로 승격하는 메서드
    public void makeAdmin(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null && !user.getRole().equals(UserRole.ROLE_ADMIN)) {
            user.setRole(UserRole.ROLE_ADMIN);
            userRepository.save(user);
        }
    }

    // 사용자가 관리자 역할을 가지고 있는지 확인하는 메서드
    public boolean isAdmin(String email) {
        User user = userRepository.findByEmail(email);

        return user != null && user.getRole().equals(UserRole.ROLE_ADMIN);
    }

    // 탈퇴 안한 회원만 조회
    public List<User> getAllActiveUsers() {
        return userRepository.findAllByDeletedFalse();
    }

    public List<User> getAllInactiveUsers() {
        return userRepository.findAll().stream()
                .filter(User::isDeleted)
                .collect(Collectors.toList());
    }

    // 탈퇴한 회원 복구
    public void restoreUser(String email) {
        User user = userRepository.findByEmail(email);

        user.setDeleted(false);
        userRepository.save(user);
    }
}
