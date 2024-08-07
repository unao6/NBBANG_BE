package com.elice.nbbang.domain.payment.controller;

import com.elice.nbbang.domain.payment.dto.CardInfoResponse;
import com.elice.nbbang.domain.payment.service.CardService;
import com.elice.nbbang.domain.payment.service.PaymentService;
import com.elice.nbbang.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/card")
@RequiredArgsConstructor
@RestController
public class CardController {

    private final CardService cardService;
    private final UserUtil userUtil;
    private final PaymentService paymentService;

    @GetMapping("/info")
    public ResponseEntity<CardInfoResponse> getCardInfo() {
        String email = userUtil.getAuthenticatedUserEmail();
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = paymentService.getAuthenticatedUserId();

        CardInfoResponse cardInfoResponse = cardService.getCardInfo(userId);
        return ResponseEntity.ok(cardInfoResponse);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteCardInfo() {
        String email = userUtil.getAuthenticatedUserEmail();
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = paymentService.getAuthenticatedUserId();

        cardService.deleteCardInfo(userId);
        return ResponseEntity.ok().build();
    }
}
