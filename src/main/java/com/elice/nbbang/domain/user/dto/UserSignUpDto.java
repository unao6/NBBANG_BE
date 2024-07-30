package com.elice.nbbang.domain.user.dto;

<<<<<<< HEAD
=======

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
>>>>>>> b174502d8a9fb8e273d2fa6e4dddfeb7f5c17d8a
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpDto {

    @NotBlank
    private String email;

    @NotBlank
    private String certificationNumber;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]{8,13}$")
    private String password;

    @NotBlank
    private String nickname;

    @NotBlank
    private String phoneNumber;

}