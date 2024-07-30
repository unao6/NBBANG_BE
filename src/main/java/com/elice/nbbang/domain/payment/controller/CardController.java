package com.elice.nbbang.domain.payment.controller;

import com.elice.nbbang.domain.payment.dto.CardInfoResponse;
import com.elice.nbbang.domain.payment.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/card")
@RequiredArgsConstructor
@RestController
public class CardController {

    private final CardService cardService;

    @GetMapping("/info/{userId}")
    public ResponseEntity<CardInfoResponse> getCardInfo(@PathVariable Long userId) {
        CardInfoResponse cardInfoResponse = cardService.getCardInfo(userId);
        return ResponseEntity.ok(cardInfoResponse);
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Void> deleteCardInfo(@PathVariable Long userId) {
        cardService.deleteCardInfo(userId);
        return ResponseEntity.ok().build();
    }


}
