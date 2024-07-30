package com.elice.nbbang.domain.payment.controller;

import com.elice.nbbang.domain.payment.dto.CardPaymentRequest;
import com.elice.nbbang.domain.payment.dto.PaymentRegisterDTO;
import com.elice.nbbang.domain.payment.dto.PaymentReserve;
import com.elice.nbbang.domain.payment.entity.Payment;
import com.elice.nbbang.domain.payment.entity.enums.PaymentStatus;
import com.elice.nbbang.domain.payment.entity.enums.PaymentType;
import com.elice.nbbang.domain.payment.service.BootPayService;
import com.elice.nbbang.domain.payment.service.CardService;
import com.elice.nbbang.domain.payment.service.PaymentService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/bootpay")
@RequiredArgsConstructor
@RestController
public class BootPayController {

    private final BootPayService bootPayService;
    private final CardService cardService;
    private final PaymentService paymentService;

    /*
    * 카드 등록 API
    * 1. 카드에 대한 빌링키를 발급받음
    * 2. DB에 카드 정보를 저장
    * */
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

    /*
    * 카드 예약 결제 API
    * 1. 빌링키를 통해 결제 예약
    * 2. DB에 결제 정보를 저장
    * */
    @PostMapping("/reserve")
    public ResponseEntity<String> reservePayment(@RequestBody PaymentReserve reserve) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            String reserveId = bootPayService.reservePayment(reserve.getBillingKey(), reserve.getAmount(), reserve.getPaymentSubscribedAt());
            paymentService.createPayment(reserve, reserveId);

            return ResponseEntity.ok("Payment reservation successful");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment reservation failed");
        }
    }

    //카드 예약 결제 조회 API
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

    /*
     * 카드 예약 결제 취소 API
     * 1. reseveId 통해 결제 취소
     * 2. DB에 결제 정보의 결제 상태를 예약 취소로 변경
     * */
    @DeleteMapping("/reserve/{reserveId}")
    public ResponseEntity<String> cancelReservation(@PathVariable("reserveId") String id) {
        try {
            bootPayService.reserveDelete(id);
            paymentService.deletePayment(id);
            return ResponseEntity.ok("Reservation cancel successful");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Reservation cancel failed");
        }
    }
}