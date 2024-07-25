//package com.elice.nbbang.domain.user.dto;
//
//import com.elice.nbbang.domain.user.entity.User;
//import com.elice.nbbang.domain.user.entity.UserRole;
//import jakarta.validation.constraints.NotBlank;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//@Getter
//@NoArgsConstructor
//public class UserSignUpDto {
//
//    @NotBlank(message = "이메일을 입력하세요.")
//    private String email;
//
//    @NotBlank(message = "비밀번호를 입력하세요.")
//    private String password;
//    private String passwordCheck;
//
//    @NotBlank(message = "닉네임을 입력하세요.")
//    private String nickname;
//
//    public User toEntity() {
//        return User.builder()
//                .email(this.email)
//                .password(this.password)
//                .nickname(this.nickname)
//                .role(UserRole.USER)
//                .build();
//    }
//}
