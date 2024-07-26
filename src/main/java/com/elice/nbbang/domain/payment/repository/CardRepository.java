package com.elice.nbbang.domain.payment.repository;

import com.elice.nbbang.domain.payment.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Card save(Card card);
}
