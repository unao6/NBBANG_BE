package com.elice.nbbang.domain.payment.service;

import com.elice.nbbang.domain.payment.dto.CardPaymentRequest;
import com.elice.nbbang.domain.payment.dto.CardRegisterDTO;
import com.elice.nbbang.domain.payment.entity.Card;
import com.elice.nbbang.domain.payment.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CardService {

    private final CardRepository cardRepository;

    //카드 등록
    public Card registerCard(CardRegisterDTO registerDTO) {
        Card card = Card.builder()
            .billingKey(registerDTO.getBillingKey())
            .cardNumber(registerDTO.getCardNumber())
            .cardCompany(registerDTO.getCardCompany())
            .build();
        cardRepository.save(card);
        return card;
    }
}
