package com.elice.nbbang.domain.user.service;

import com.elice.nbbang.domain.user.dto.EmailCertificationRequestDto;
import com.elice.nbbang.domain.user.dto.UserSignUpDto;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.entity.UserRole;
import com.elice.nbbang.domain.user.provider.CertificationNumber;
import com.elice.nbbang.domain.user.provider.EmailProvider;
import com.elice.nbbang.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailProvider emailProvider;

    public boolean signUpProcess(UserSignUpDto userSignUpDto) {
        String email = userSignUpDto.getEmail();
        String password = userSignUpDto.getPassword();
        String nickname = userSignUpDto.getNickname();
        String phoneNumber = userSignUpDto.getPhoneNumber();

        if (userRepository.existsByEmail(email)) {
            return false;
        }
        if (userRepository.existsByNickname(nickname)) {
            return false;
        }

        String encodedPassword = bCryptPasswordEncoder.encode(password);

        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .phoneNumber(phoneNumber)
                .role(UserRole.USER)
                .build();

        userRepository.save(user);
        return true;
    }

    public boolean emailCertification(EmailCertificationRequestDto emailCertificationRequestDto) {
        String email = emailCertificationRequestDto.getEmail();

        String certificationNumber = CertificationNumber.getCertificationNumber();
        boolean isSuccessed = emailProvider.sendCertificationMail(email, certificationNumber);
        return isSuccessed;
    }
}
