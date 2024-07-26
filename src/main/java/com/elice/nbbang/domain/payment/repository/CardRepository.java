package com.elice.nbbang.domain.payment.repository;

import com.elice.nbbang.domain.payment.entity.Card;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByUserUserId(Long userId);
}
