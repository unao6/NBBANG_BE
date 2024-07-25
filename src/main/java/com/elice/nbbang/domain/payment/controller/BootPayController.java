package com.elice.nbbang.domain.payment.controller;

import com.elice.nbbang.domain.payment.dto.CardPaymentRequest;
import com.elice.nbbang.domain.payment.dto.CardRegisterDTO;
import com.elice.nbbang.domain.payment.entity.Card;
import com.elice.nbbang.domain.payment.service.BootPayService;
import com.elice.nbbang.domain.payment.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/payment")
@RequiredArgsConstructor
@RestController
public class BootPayController {

    private final BootPayService bootPayService;
    private final CardService cardService;

    //카드 등록 API
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/card")
    public ResponseEntity<String> registerCard(@RequestBody CardPaymentRequest request) {
        try {
            String billingKey = bootPayService.getBillingKey(request.getReceiptId());
            CardRegisterDTO dto = CardRegisterDTO.builder()
                .billingKey(billingKey)
                .cardNumber(request.getCardNumber())
                .cardCompany(request.getCardCompany())
                .build();
            cardService.registerCard(dto);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok("Payment completed");
    }

    //카카오페이 등록 API
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/kakaopay")
    public ResponseEntity<String> registerKakaoPay(@RequestBody CardPaymentRequest request) {
        try {
            String billingKey = bootPayService.getBillingKey(request.getReceiptId());
            CardRegisterDTO dto = CardRegisterDTO.builder()
                .billingKey(billingKey)
                .cardNumber(request.getCardNumber())
                .cardCompany(request.getCardCompany())
                .build();
            cardService.registerCard(dto);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok("Payment completed");
    }
}