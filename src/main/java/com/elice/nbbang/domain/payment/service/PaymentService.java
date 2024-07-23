package com.elice.nbbang.domain.payment.service;

import com.elice.nbbang.domain.payment.dto.CardRegistrationDTO;
import com.elice.nbbang.domain.payment.entity.Card;
import com.elice.nbbang.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
}
