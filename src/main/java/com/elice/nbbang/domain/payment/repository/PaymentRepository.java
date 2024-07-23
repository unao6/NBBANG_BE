package com.elice.nbbang.domain.payment.repository;

import com.elice.nbbang.domain.payment.entity.Card;
import com.elice.nbbang.domain.payment.entity.Payment;
import com.elice.nbbang.domain.payment.entity.enums.TransactionType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findAll(); //전체 거래 조회

    List<Payment> findByTransactionType(TransactionType transactionType); //거래 종류에 따른 검색 기능
}