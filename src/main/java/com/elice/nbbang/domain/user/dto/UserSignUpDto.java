package com.elice.nbbang.domain.user.dto;

import com.elice.nbbang.domain.auth.dto.request.PhoneCerfiticationRequestDto;
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
    private PhoneCerfiticationRequestDto phoneCerfiticationRequestDto;

}