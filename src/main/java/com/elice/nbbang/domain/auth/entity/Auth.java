package com.elice.nbbang.domain.auth.entity;

import com.elice.nbbang.global.util.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Auth extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_id")
    private Long authId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    private Provider provider;

    @Column(name = "provider_id", length = 255)
    private String providerId;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    // Enum type for provider
    public enum Provider {
        GOOGLE, FACEBOOK, GITHUB // 필요한 다른 provider 값 추가 가능
    }
}