package com.elice.nbbang.domain.user.service;

import com.elice.nbbang.domain.auth.dto.request.CheckCertificationRequestDto;
import com.elice.nbbang.domain.auth.dto.request.EmailCertificationRequestDto;
import com.elice.nbbang.domain.auth.dto.request.PhoneCerfiticationRequestDto;
import com.elice.nbbang.domain.user.dto.UserSignUpDto;
import com.elice.nbbang.domain.auth.entity.MailCertification;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.entity.UserRole;
import com.elice.nbbang.domain.auth.provider.CertificationNumber;
import com.elice.nbbang.domain.auth.provider.UserEmailProvider;
import com.elice.nbbang.domain.auth.repository.MailRepository;
import com.elice.nbbang.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpService {

    private final UserRepository userRepository;
    private final MailRepository mailRepository;
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

        // PhoneCerfiticationRequestDto 객체로부터 phoneNumber를 추출
        PhoneCerfiticationRequestDto phoneCerfiticationRequestDto = userSignUpDto.getPhoneCerfiticationRequestDto();
        String phoneNumber = phoneCerfiticationRequestDto != null ? phoneCerfiticationRequestDto.getPhoneNumber() : null;

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

    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    public boolean isNicknameAvailable(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    public boolean emailCertification(EmailCertificationRequestDto emailCertificationRequestDto) {
        String email = emailCertificationRequestDto.getEmail();

        // 이메일 인증 번호 생성 및 전송
        String certificationNumber = CertificationNumber.getCertificationNumber();

        // MailCertification 객체 생성
        MailCertification mailCertification = new MailCertification();
        mailCertification.setEmail(email);
        mailCertification.setCertificationNumber(certificationNumber);
        mailCertification.setVerified(false);

        // MailCertification 객체를 데이터베이스에 저장
        mailRepository.save(mailCertification);

        return emailProvider.sendCertificationMail(email, certificationNumber);
    }

    public boolean checkCertification(CheckCertificationRequestDto checkCertificationRequestDto) {
        String email = checkCertificationRequestDto.getEmail();
        String certificationNumber = checkCertificationRequestDto.getCertificationNumber();


        MailCertification mailCertification = mailRepository.findByEmail(email);

        if (mailCertification == null) return false;

        boolean isMatched = mailCertification.getEmail().equals(email) && mailCertification.getCertificationNumber().equals(certificationNumber);
        if (!isMatched) return false;

        mailCertification.setVerified(true);
        mailRepository.save(mailCertification);

        return true;
    }

}
