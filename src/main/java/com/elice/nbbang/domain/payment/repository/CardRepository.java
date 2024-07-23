package com.elice.nbbang.domain.payment.repository;

import com.elice.nbbang.domain.payment.entity.Card;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Card save(Card card); //카드 등록

    Optional<Card> findById(Long id); //카드 조회

    void deleteById(Long id); //카드 삭제

    /* test */
    List<Card> findAll();
}