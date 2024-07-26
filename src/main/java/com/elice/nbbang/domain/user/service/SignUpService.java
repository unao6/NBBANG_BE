package com.elice.nbbang.domain.user.service;

import com.elice.nbbang.domain.user.dto.UserSignUpDto;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.entity.UserRole;
import com.elice.nbbang.domain.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SignUpService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public SignUpService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {

        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;

    }

    //리턴 타입은 불린으로 회원가입 됐으면 트루 안됐으면 false로 해야함 -> 컨트롤러에서 회원가입 됐는지 안됐는지 메세지 보내줄 수 있음
    //jwt 주제로 하기 때문에 일단 간단하게 void로
    public void signUpProcess(UserSignUpDto userSignUpDto) {

        String email = userSignUpDto.getEmail();
        String password = userSignUpDto.getPassword();
        String nickname = userSignUpDto.getNickname();
        String phoneNumber = userSignUpDto.getPhoneNumber();

        //이메일 전달해서 존재하는지 보고 존재하면 강제 종료
        Boolean isExist = userRepository.existsByEmail(email);

        if (isExist) {

            return;
        }

        // 비밀번호 암호화
        String encodedPassword = bCryptPasswordEncoder.encode(password);

        // 암호화된 비밀번호로 User 객체 생성
        User data = User.builder()
                .email(email)
                .password(encodedPassword) // 암호화된 비밀번호를 직접 전달
                .nickname(nickname)
                .phoneNumber(phoneNumber)
                .role(UserRole.USER)
                .build();


        userRepository.save(data);
    }
}
