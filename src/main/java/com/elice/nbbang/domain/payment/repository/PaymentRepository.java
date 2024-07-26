package com.elice.nbbang.domain.payment.repository;


import com.elice.nbbang.domain.payment.entity.Payment;
import com.elice.nbbang.domain.payment.enums.PaymentStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTid(String tid);

    //모든 결제를 최신순 조회
    List<Payment> findAllByOrderByPaymentCreatedAtDesc();

    //특정 유저의 결제를 최신순 조회
    List<Payment> findByUserUserIdOrderByPaymentCreatedAtDesc(Long userId);

    //결제 상태별 최신순 조회
    List<Payment> findByStatusOrderByPaymentCreatedAtDesc(PaymentStatus status);
}
