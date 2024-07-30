package com.elice.nbbang.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpDto {

    private String email;
    private String password;
    private String nickname;
    private String phoneNumber;

}