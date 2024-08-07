package com.elice.nbbang.domain.payment.service;

import com.elice.nbbang.domain.payment.dto.CardInfoResponse;
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
    private final BootPayService bootPayService;

    public CardInfoResponse getCardInfo(Long userId) {
        Card userCard = cardRepository.findByUserId(userId).orElse(null);
        if (userCard.getIssuerCorp() == null) { //카드
            return cardRepository.findByUserId(userId)
                .map(card -> CardInfoResponse.builder()
                    .cardCompany(userCard.getCardCompany())
                    .cardNumber(userCard.getCardNumber())
                    .build())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 카드 정보가 없습니다."));
        } else { //카카오페이
            return cardRepository.findByUserId(userId)
                .map(card -> CardInfoResponse.builder()
                    .issuerCorp(userCard.getIssuerCorp())
                    .cardType(userCard.getCardType())
                    .cardCompany(userCard.getCardCompany())
                    .build())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저의 카드 정보가 없습니다."));
        }
    }

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

    //카드 삭제
    public void deleteCardInfo(Long userId) {
        Card card = cardRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("해당 유저의 카드 정보가 없습니다."));
        //발급받은 빌링키 취소
        try {
            bootPayService.deleteBillingKey(card.getBillingKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        cardRepository.delete(card);
    }
}
