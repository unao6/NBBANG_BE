package com.elice.nbbang.domain.user.repository;

import com.elice.nbbang.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
