package com.elice.nbbang.domain.user.repository;

import com.elice.nbbang.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    //로그인 이메일을 갖는 객체가 존재하는지 -> 존재하면 true 리턴 (ID 중복 검사 시 필요)
    Boolean existsByEmail(String email);

    User findByEmail(String email);
}