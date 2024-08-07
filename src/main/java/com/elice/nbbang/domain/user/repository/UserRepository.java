package com.elice.nbbang.domain.user.repository;

import com.elice.nbbang.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    //로그인 이메일을 갖는 객체가 존재하는지 -> 존재하면 true 리턴 (ID 중복 검사 시 필요)
    Boolean existsByEmail(String email);

    //닉네임 중복검사
    Boolean existsByNickname(String nickname);

    User findByEmail(String email);

    List<User> findAllByDeletedFalse();

    Optional<User> findByEmailAndDeletedFalse(String email);

}