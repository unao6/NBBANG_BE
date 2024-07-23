package com.elice.nbbang.domain.payment.service;

import com.elice.nbbang.domain.payment.dto.CardRegistrationDTO;
import com.elice.nbbang.domain.payment.entity.Card;
import com.elice.nbbang.domain.payment.exception.ResourceNotFoundException;
import com.elice.nbbang.domain.payment.repository.CardRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CardService {

    private final CardRepository cardRepository;

    //카드 등록
    public Card registerCard(CardRegistrationDTO cardRegistrationDTO) {
        Card card = new Card();
        card.setCardNumber(cardRegistrationDTO.getCardNumber());
        card.setCardHolder(cardRegistrationDTO.getCardHolder());
        card.setCardCompany(cardRegistrationDTO.getCardCompany());
        card.setBillingKey(cardRegistrationDTO.getBillingKey());
        cardRepository.save(card);
        return card;
    }

    //카드 변경
    public Card changeCard(Long cardId, CardRegistrationDTO cardRegistrationDTO) {
        Card selectedCard = cardRepository.findById(cardId)
            .orElseThrow(() -> new ResourceNotFoundException("해당하는 ID의 카드가 없습니다."));

        selectedCard.setId(cardId);
        selectedCard.setCardNumber(cardRegistrationDTO.getCardNumber());
        selectedCard.setCardHolder(cardRegistrationDTO.getCardHolder());
        selectedCard.setCardCompany(cardRegistrationDTO.getCardCompany());
        selectedCard.setBillingKey(cardRegistrationDTO.getBillingKey());
        cardRepository.save(selectedCard);
        return selectedCard;
    }

    //카드 삭제
    public void removeCard(Long cardId) {
        Card selectedCard = cardRepository.findById(cardId)
            .orElseThrow(() -> new ResourceNotFoundException("해당하는 ID의 카드가 없습니다."));

        cardRepository.deleteById(cardId);
    }

    /* test */
    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }
}
