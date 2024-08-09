package com.elice.nbbang.domain.auth.repository;

import com.elice.nbbang.domain.auth.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<Auth, Long> {

    // 사용자 ID로 Auth 엔티티를 조회하는 메서드
    Optional<Auth> findByUserId(Long userId);

    // provider와 providerId로 Auth 엔티티를 조회하는 메서드
    Optional<Auth> findByProviderAndProviderId(Auth.Provider provider, String providerId);
}