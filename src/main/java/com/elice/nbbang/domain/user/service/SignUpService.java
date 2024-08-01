package com.elice.nbbang.domain.user.service;

import com.elice.nbbang.domain.user.dto.CheckCertificationRequestDto;
import com.elice.nbbang.domain.user.dto.EmailCertificationRequestDto;
import com.elice.nbbang.domain.user.dto.UserSignUpDto;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.entity.UserRole;
import com.elice.nbbang.domain.user.provider.CertificationNumber;
import com.elice.nbbang.domain.user.provider.UserEmailProvider;
import com.elice.nbbang.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserEmailProvider emailProvider;

    public boolean signUpProcess(UserSignUpDto userSignUpDto) {
        String email = userSignUpDto.getEmail();
        if (userRepository.existsByEmail(email)) {
            return false;
        }

        String password = userSignUpDto.getPassword();
        String encodedPassword = bCryptPasswordEncoder.encode(password);

        String nickname = userSignUpDto.getNickname();
        if (userRepository.existsByNickname(nickname)) {
            return false;
        }

        String phoneNumber = userSignUpDto.getPhoneNumber();

        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .phoneNumber(phoneNumber)
                .role(UserRole.ROLE_USER)
                .build();

        userRepository.save(user);
        return true;
    }

    public boolean emailCertification(EmailCertificationRequestDto emailCertificationRequestDto) {
        String email = emailCertificationRequestDto.getEmail();

        // 이메일 인증 번호 생성 및 전송
        String certificationNumber = CertificationNumber.getCertificationNumber();
        return emailProvider.sendCertificationMail(email, certificationNumber);
    }
}
