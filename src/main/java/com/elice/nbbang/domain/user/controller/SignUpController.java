package com.elice.nbbang.domain.user.controller;

import com.elice.nbbang.domain.user.dto.CheckCertificationRequestDto;
import com.elice.nbbang.domain.user.dto.EmailCertificationRequestDto;
import com.elice.nbbang.domain.user.dto.UserSignUpDto;
import com.elice.nbbang.domain.user.service.SignUpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class SignUpController {

    private final SignUpService signUpService;

    @PostMapping("/sign-up")
    public ResponseEntity<String> signUpProcess(@RequestBody UserSignUpDto userSignUpDto) {
        boolean isSignedUp = signUpService.signUpProcess(userSignUpDto);

        if (isSignedUp) {
            return ResponseEntity.status(HttpStatus.CREATED).body("User signed up successfully");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("fail");
        }
    }

    @PostMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestBody UserSignUpDto userSignUpDto) {
        boolean isAvailable = signUpService.isEmailAvailable(userSignUpDto.getEmail());

        if (isAvailable) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(false);
        }
    }

    @PostMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestBody UserSignUpDto userSignUpDto) {
        boolean isAvailable = signUpService.isNicknameAvailable(userSignUpDto.getNickname());

        if (isAvailable) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(false);
        }
    }

    @PostMapping("/email-certification")
    public ResponseEntity<?> emailCertification(@RequestBody EmailCertificationRequestDto emailCertificationRequestDto) {
        boolean isCertified = signUpService.emailCertification(emailCertificationRequestDto);

        if (isCertified) {
            return ResponseEntity.status(HttpStatus.OK).body("The email has been successfully sent.");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Failed to send the email.");
        }
    }
}
