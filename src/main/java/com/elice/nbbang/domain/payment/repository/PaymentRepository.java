package com.elice.nbbang.domain.payment.repository;


import com.elice.nbbang.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
