package com.elice.nbbang.domain.Card.repository;

import com.elice.nbbang.domain.Card.entity.Card;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByUserUserId(Long userId);
}
