package com.elice.nbbang.domain.user.controller;

import com.elice.nbbang.domain.user.dto.UserSignUpDto;
import com.elice.nbbang.domain.user.service.SignUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SignUpController {

    private final SignUpService signUpService;

    @PostMapping("/users/sign-up")
    public ResponseEntity<String> signUpProcess(@RequestBody UserSignUpDto userSignUpDto) {

        signUpService.signUpProcess(userSignUpDto);

        //나중에 회원가입 됐는지 안됐는지에 따라 리턴 값 다르게 바꿔주기. 일단은 되던 안되던 ok로 표시
        return ResponseEntity.status(HttpStatus.CREATED).body("User signed up successfully");
    }
}
