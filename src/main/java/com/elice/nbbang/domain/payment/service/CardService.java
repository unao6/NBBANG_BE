package com.elice.nbbang.domain.payment.service;

import com.elice.nbbang.domain.payment.dto.CardInfoResponse;
import com.elice.nbbang.domain.payment.dto.CardPaymentRequest;
import com.elice.nbbang.domain.payment.dto.CardRegisterDTO;
import com.elice.nbbang.domain.payment.entity.Card;
import com.elice.nbbang.domain.payment.entity.enums.CardStatus;
import com.elice.nbbang.domain.payment.entity.enums.PaymentType;
import com.elice.nbbang.domain.payment.repository.CardRepository;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.repository.UserRepository;
import com.elice.nbbang.domain.user.service.UserUtilService;
import com.elice.nbbang.global.exception.CustomException;
import com.elice.nbbang.global.exception.ErrorCode;
import com.elice.nbbang.global.util.UserUtil;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CardService {

    private final CardRepository cardRepository;
    private final BootPayService bootPayService;
    private final UserUtil userUtil;
    private final UserRepository userRepository;
    private final UserUtilService userUtilService;

    public CardInfoResponse getCardInfo(Long userId) {
        Optional<Card> userCardOptional = cardRepository.findByUserId(userId);

        // 카드가 없는 경우 CustomException을 던집니다.
        if (userCardOptional.isEmpty()) {
            throw new CustomException(ErrorCode.CARD_NOT_FOUND);
        }

        Card userCard = userCardOptional.get();

        if (userCard.getPaymentType() == PaymentType.CARD) { // 일반 카드
            return CardInfoResponse.builder()
                .cardCompany(userCard.getCardCompany())
                .cardNumber(userCard.getCardNumber())
                .build();
        } else { // 카카오페이
            return CardInfoResponse.builder()
                .issuerCorp(userCard.getIssuerCorp())
                .cardType(userCard.getCardType())
                .cardCompany(userCard.getCardCompany())
                .build();
        }
    }

    //카드 등록 or 변경
    public Card registerCard(CardPaymentRequest request, String billingKey) {
        User user = userUtilService.getUserByEmail();

        Card existingCard = cardRepository.findByUserId(user.getId()).orElse(null);
        if (existingCard != null) {
            cardRepository.delete(existingCard);
            cardRepository.flush();
            Card card = Card.builder()
                .user(user)
                .billingKey(billingKey)
                .cardNumber(request.getCardNumber())
                .cardCompany(request.getCardCompany())
                .cardStatus(CardStatus.AVAILABLE)
                .paymentType(PaymentType.CARD)
                .build();
            cardRepository.save(card);
            return card;
        }

        Card card = Card.builder()
            .user(user)
            .billingKey(billingKey)
            .cardNumber(request.getCardNumber())
            .cardCompany(request.getCardCompany())
            .cardStatus(CardStatus.AVAILABLE)
            .paymentType(PaymentType.CARD)
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
