package com.elice.nbbang.domain.payment.controller;

import com.elice.nbbang.domain.payment.dto.CardPaymentRequest;
import com.elice.nbbang.domain.payment.dto.CardRegisterDTO;
import com.elice.nbbang.domain.payment.dto.PaymentReserve;
import com.elice.nbbang.domain.payment.entity.Card;
import com.elice.nbbang.domain.payment.service.BootPayService;
import com.elice.nbbang.domain.payment.service.CardService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
            cardService.registerCard(request, billingKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok("Payment completed");
    }

    //예약 결제 API
    @PostMapping("/reserve")
    public ResponseEntity<String> reservePayment(@RequestBody PaymentReserve reserve) {
        try {
            LocalDate localDate = LocalDate.parse(reserve.getPaymentTime()); // Make sure the date format is handled correctly
            bootPayService.reservePayment(reserve.getBillingKey(), reserve.getAmount(), localDate);
            return ResponseEntity.ok("Payment reservation successful");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment reservation failed");
        }
    }

    //예약 결제 조회 API
    @GetMapping("/reserve/{reserveId}")
    public ResponseEntity<String> lookupReservation(@PathVariable("reserveId") String id) {
        try {
            bootPayService.reserveLookup(id);
            return ResponseEntity.ok("Reservation lookup successful");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Reservation lookup failed");
        }
    }

    //예약 결제 취소 API
    @DeleteMapping("/reserve/{reserveId}")
    public ResponseEntity<String> cancelReservation(@PathVariable("reserveId") String id) {
        try {
            bootPayService.reserveLookup(id);
            return ResponseEntity.ok("Reservation cancel successful");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Reservation cancel failed");
        }
    }
}