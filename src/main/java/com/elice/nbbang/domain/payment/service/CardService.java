package com.elice.nbbang.domain.payment.service;

import com.elice.nbbang.domain.payment.dto.CardPaymentRequest;
import com.elice.nbbang.domain.payment.dto.CardRegisterDTO;
import com.elice.nbbang.domain.payment.entity.Card;
import com.elice.nbbang.domain.payment.entity.enums.CardStatus;
import com.elice.nbbang.domain.payment.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CardService {

    private final CardRepository cardRepository;

    //카드 등록
    public Card registerCard(CardPaymentRequest request, String billingKey) {
        Card card = Card.builder()
            .billingKey(billingKey)
            .cardNumber(request.getCardNumber())
            .cardCompany(request.getCardCompany())
            .cardStatus(CardStatus.AVAILABLE)
            .build();
        cardRepository.save(card);
        return card;
    }
}
