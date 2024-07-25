//package com.elice.nbbang.domain.user.entity;
//
//import com.elice.nbbang.global.util.BaseTimeEntity;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Builder
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//public class User extends BaseTimeEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name="user_id")
//    private long id;
//
//    @Column(nullable = false)
//    private String email;
//
//    @Column(nullable = false)
//    private String password;
//
//    @Column
//    private String nickname;
//
//    @Enumerated(EnumType.STRING)
//    private UserRole role;
//
//    @Column
//    private String phoneNumber;
//}