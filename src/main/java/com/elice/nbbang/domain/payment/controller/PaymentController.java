package com.elice.nbbang.domain.payment.controller;

import com.elice.nbbang.domain.payment.dto.CardRegistrationRequest;
import com.elice.nbbang.domain.payment.entity.Card;
import com.elice.nbbang.domain.payment.service.CardService;
import com.elice.nbbang.domain.payment.service.PaymentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/payment")
@RequiredArgsConstructor
@RestController
public class PaymentController {

    private final PaymentService paymentService;
    private final CardService cardService;

    //카드 조회 API
    //userID를 통해 조회할 수 있도록 수정 필요
    @GetMapping("card")
    public ResponseEntity<List<Card>> viewCards() {
        List<Card> cards = cardService.getAllCards();
        return ResponseEntity.status(HttpStatus.OK).body(cards);
    }

    //카드 등록 API
    @PostMapping("card")
    public ResponseEntity<Card> createCard(@RequestBody CardRegistrationRequest cardRegistrationDTO) {
        Card registeredCard = cardService.registerCard(cardRegistrationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredCard);
    }

    //카드 변경 API
    @PutMapping("card/{cardId}")
    public ResponseEntity<Card> updateCard(@PathVariable("cardId") Long cardId, @RequestBody CardRegistrationRequest cardRegistrationDTO) {
        Card updatedCard = cardService.changeCard(cardId, cardRegistrationDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedCard);
    }

    //카드 삭제 API
    @DeleteMapping("card/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable("cardId") Long cardId) {
        cardService.removeCard(cardId);
        return ResponseEntity.noContent().build(); //응답할 본문이 없다는 것을 명확하게 표시
    }
}