package com.elice.nbbang.domain.user.dto;

import com.elice.nbbang.domain.auth.dto.request.PhoneCertificationRequestDto;
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
    private PhoneCertificationRequestDto phoneCertificationRequestDto;

}