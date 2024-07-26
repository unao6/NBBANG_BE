package com.elice.nbbang.domain.Card.service;

import com.elice.nbbang.domain.Card.dto.CardInfoResponse;
import com.elice.nbbang.domain.Card.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    public CardInfoResponse getCardInfo(Long userId) {
        return cardRepository.findByUserUserId(userId)
                .map(card -> new CardInfoResponse(card.getIssuerCorp(), card.getCardType()))
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 카드 정보가 없습니다."));
    }
}
