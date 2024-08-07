package com.elice.nbbang.domain.user.service;

import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.repository.UserRepository;
import com.elice.nbbang.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserUtilService {

    private final UserRepository userRepository;
    private final UserUtil userUtil;

    //이메일을 통한 유저 조회
    public User getUserByEmail() {
        String email = userUtil.getAuthenticatedUserEmail();
        if (email == null) {
            throw new IllegalArgumentException("해당하는 이메일이 없습니다.");
        }

        return userRepository.findByEmail(email);
    }
}
