package com.elice.nbbang.domain.user.service;


import com.elice.nbbang.domain.auth.dto.OAuth2Response;
import com.elice.nbbang.domain.auth.dto.request.PhoneCheckRequestDto;
import com.elice.nbbang.domain.auth.service.MessageService;
import com.elice.nbbang.domain.user.dto.reponse.UserResponse;
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
    private final MessageService messageService;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // 휴대폰 번호 변경
    public boolean changePhoneNumber(String email, String newPhoneNumber, String randomNumber) {
        // 인증 코드 확인
        PhoneCheckRequestDto phoneCheckRequestDto = new PhoneCheckRequestDto(newPhoneNumber, randomNumber);
        String verificationResult = messageService.verifySms(phoneCheckRequestDto);

        // verificationResult를 확인하여 인증 성공 여부 결정
        boolean isVerified = "success".equalsIgnoreCase(verificationResult); // "success"가 반환되는 경우 인증 성공으로 간주

        if (!isVerified) {
            throw new IllegalArgumentException("휴대폰 인증이 완료되지 않았습니다.");
        }

        // 유저 정보 가져오기
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException(ErrorCode.USER_NOT_FOUND);
        }

        // 휴대폰 번호 변경
        user.setPhoneNumber(newPhoneNumber);
        userRepository.save(user);

        return true;
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