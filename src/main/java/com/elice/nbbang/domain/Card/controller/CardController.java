package com.elice.nbbang.domain.Card.controller;

import com.elice.nbbang.domain.Card.dto.CardInfoResponse;
import com.elice.nbbang.domain.Card.entity.Card;
import com.elice.nbbang.domain.Card.repository.CardRepository;
import com.elice.nbbang.domain.Card.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
}
