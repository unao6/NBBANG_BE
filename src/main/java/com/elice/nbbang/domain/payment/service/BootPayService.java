package com.elice.nbbang.domain.payment.service;

import static com.elice.nbbang.global.exception.ErrorCode.PAYMENT_NOT_FOUND;
import static org.hibernate.query.sqm.tree.SqmNode.log;

import com.elice.nbbang.domain.ott.entity.Ott;
import com.elice.nbbang.domain.ott.exception.OttNotFoundException;
import com.elice.nbbang.domain.ott.repository.OttRepository;
import com.elice.nbbang.domain.payment.dto.PaymentReserve;
import com.elice.nbbang.domain.payment.entity.Payment;
import com.elice.nbbang.domain.payment.entity.enums.PaymentStatus;
import com.elice.nbbang.domain.payment.entity.enums.PaymentType;
import com.elice.nbbang.domain.payment.repository.PaymentRepository;
import com.elice.nbbang.global.config.EncryptUtils;
import com.elice.nbbang.global.exception.CustomException;
import com.elice.nbbang.global.exception.ErrorCode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;
import kr.co.bootpay.Bootpay;
import kr.co.bootpay.model.request.Cancel;
import kr.co.bootpay.model.request.SubscribePayload;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BootPayService {

    private static final String ORDER_NAME = "엔빵 예약결제";
    private final Bootpay bootpay;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final OttRepository ottRepository;
    private final EncryptUtils encryptUtils;

    public BootPayService(@Value("${bootpay.applicationId}") String applicationId,
        @Value("${bootpay.privateKey}") String privateKey, PaymentService paymentService, PaymentRepository paymentRepository,
        OttRepository ottRepository, EncryptUtils encryptUtils) {
        this.bootpay = new Bootpay(applicationId, privateKey);
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
        this.ottRepository = ottRepository;
        this.encryptUtils = encryptUtils;
    }

    //빌링키 조회
    public String getBillingKey(String receiptId) throws Exception {
        bootpay.getAccessToken();

        try {
            HashMap<String, Object> res = bootpay.lookupBillingKey(receiptId);
            JSONObject json = new JSONObject(res);

            if (res.get("error_code") == null) {
                System.out.println("getKey success");
            } else {
                System.out.println("getKey false");
            }

            return res.get("billing_key").toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("에러 발생");
        }
    }

    //빌링키 삭제
    public void deleteBillingKey(String billingKey) throws Exception {
        bootpay.getAccessToken();

        try {
            HashMap<String, Object> res = bootpay.destroyBillingKey(billingKey);
            JSONObject json = new JSONObject(res);

            if (res.get("error_code") == null) {
                System.out.println("destroyBillingKey success");
            } else {
                System.out.println("destroyBillingKey false");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //결제 예약
    @Transactional(readOnly = false)
    public void reservePayment(PaymentReserve reserve) throws Exception {
        bootpay.getAccessToken();

        Ott ott = reserve.getOtt();
        int amount = (ott.getPrice() / ott.getCapacity()) + PaymentService.FEE;
        String decryptedBillingKey = encryptUtils.decrypt(reserve.getBillingKey());

        SubscribePayload payload = new SubscribePayload();
        payload.billingKey = decryptedBillingKey;
        payload.orderName = ORDER_NAME;
        payload.price = amount;
        payload.orderId = "" + (System.currentTimeMillis() / 1000);

        Date date = Date.from(reserve.getPaymentSubscribedAt().atZone(ZoneId.of("UTC")).minusHours(9).plusMinutes(1).toInstant());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        payload.reserveExecuteAt = sdf.format(date);

        try {
            HashMap<String, Object> res = bootpay.reserveSubscribe(payload);
            JSONObject json = new JSONObject(res);

            if (res.get("error_code") == null) {
                System.out.println("reserveSubscribe success");

                String reserveId = res.get("reserve_id").toString();

                paymentService.createPayment(reserve, reserveId, amount);
            } else {
                System.out.println("reserveSubscribe false" + res);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("에러 발생");
        }
    }

    //예약된 결제 조회
    public HashMap<String, Object> reserveLookup(String reserveId) throws Exception {
        bootpay.getAccessToken();

        try {
            HashMap<String, Object> res = bootpay.reserveSubscribeLookup(reserveId);
            JSONObject json = new JSONObject(res);

            if (res.get("error_code") == null) {
                System.out.println("getReceipt success");
            } else {
                System.out.println("getReceipt false");
            }

            HashMap<String, Object> response = new HashMap<>();
            response.put("status", res.get("status"));
            response.put("receipt_id", res.get("receipt_id"));
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("에러 발생");
        }
    }

    //예약된 결제 취소
    @Transactional(readOnly = false)
    public void reserveDelete(String reserveId) throws Exception {
        bootpay.getAccessToken();

        try {
            HashMap<String, Object> res = bootpay.reserveCancelSubscribe(reserveId);
            JSONObject json = new JSONObject(res);

            if (res.get("error_code") == null) {
                System.out.println("getReceipt success");
                paymentService.deletePayment(reserveId);
            } else {
                System.out.println("getReceipt false");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //payment 조회
    public Payment getPaymentByReserveId(String id) {
        return paymentRepository.findByReserveId(id).orElse(null);
    }

    //예약완료된 payment를 List로 반환
    public List<String> getAllReservedPayment() {
        List<Payment> paymentList = paymentRepository.findAllByStatus(PaymentStatus.RESERVE_COMPLETED);

        List<String> reserveIds = paymentList.stream()
            .map(Payment::getReserveId)
            .collect(Collectors.toList());

        return reserveIds;
    }

    //예약완료된 payment를 주기마다 조회
    //@Scheduled(fixedRate = 60000)
    @Transactional(readOnly = false)
    public void scheduledLookupReservation() {
        List<String> reserveIds = getAllReservedPayment();

        for (String id : reserveIds) {
            lookupReservation(id);
        }
    }

    //구독일이 지난 결제 진행, 다음회차 정기결제 예약
    @Transactional(readOnly = false)
    public void lookupReservation(String id) {
        try {
            HashMap<String, Object> response = reserveLookup(id);

            if (response.get("status").toString().equals("1")) {
                Payment payment = getPaymentByReserveId(id);

                String receiptId = response.get("receipt_id").toString();
                if (receiptId != null) {
                    String encryptedReceiptId = encryptUtils.encrypt(receiptId);

                    payment.updateCompletePayment(PaymentStatus.COMPLETED, LocalDateTime.now(), encryptedReceiptId);
                    paymentRepository.save(payment);

                    //정기결제 30일 후 새로운 정기결제 예약
                    LocalDateTime newPaymentTime = payment.getPaymentSubscribedAt().plusMonths(1);

                    Ott ott = ottRepository.findById(payment.getOttId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Ott입니다."));

                    PaymentReserve reserve = PaymentReserve.builder()
                        .billingKey(payment.getBillingKey())
                        .user(payment.getUser())
                        .ott(ott)
                        .paymentSubscribedAt(newPaymentTime)
                        .build();

                    reservePayment(reserve);
                } else {
                    //receiptId가 반환되지 않을 시 = 결제 실패 시 payment 상태 업데이트
                    payment.updateFailurePayment(PaymentStatus.FAILED);
                }
            } else if (response.get("status").toString().equals("3")) {
                Payment payment = getPaymentByReserveId(id);
                payment.updateSubscribtionPayment(PaymentStatus.FAILED, payment.getPaymentSubscribedAt());
                paymentRepository.save(payment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //파티원 환불 로직
    @Transactional(readOnly = false)
    public void refundPayment(Long userId, Long ottId) {
        Optional<Payment> paymentOptional = paymentRepository.findTopByUserIdAndOttIdOrderByPaymentApprovedAtDesc(userId, ottId);
        log.info("환불 시작합니다~~~");
        log.info("userId: " + userId + ", ottId: " + ottId);

        if (paymentOptional.isPresent()) {
            Payment payment = paymentOptional.get();

            // 결제 금액 가져오기
            int paymentAmount = payment.getAmount();
            double dayPrice = (double) paymentAmount / 30; // 일일 가격 계산

            // 결제 승인일로부터 환불 신청일까지의 일수 계산
            LocalDate paymentApprovedDate = payment.getPaymentApprovedAt().toLocalDate();
            LocalDate currentDate = LocalDate.now(); // <<테스트날짜임 //현재 날짜를 환불 신청일로 간주
            long daysUsed = ChronoUnit.DAYS.between(paymentApprovedDate, currentDate);

            // 사용한 일수만큼의 금액을 계산하여 환불금액 계산 수수료도 더해서 차감
            double amountUsed = dayPrice * daysUsed + PaymentService.FEE;
            int refundAmount = (int) Math.max(0, paymentAmount - amountUsed); // 환불 금액이 음수가 되지 않도록 함.

            // Payment 객체의 상태 업데이트
            payment.updateRefundPayment(PaymentStatus.REFUND_REQUESTED, refundAmount, LocalDateTime.now());

            //부트페이 로직 호출
            try {
                cancelPayment(payment.getReceiptId(), (double) refundAmount);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 변경사항을 데이터베이스에 저장
            paymentRepository.save(payment);
        } else {
            throw new CustomException(PAYMENT_NOT_FOUND);
        }
    }

    //완료된 결제 취소
    @Transactional(readOnly = false)
    public void cancelPayment(String receiptId, Double cancelAmount) throws Exception {
        bootpay.getAccessToken();

        try {
            Cancel cancel = new Cancel();
            cancel.receiptId = encryptUtils.decrypt(receiptId);
            cancel.cancelPrice = cancelAmount;

            HashMap<String, Object> res = bootpay.receiptCancel(cancel);

            if (res.get("error_code") == null) {
                System.out.println("receiptCancel success");

                paymentService.cancelPayment(receiptId, cancelAmount);
            } else {
                System.out.println("receiptCancel false");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 재매칭 시 다음 결제일 수정 로직(예약 결제 취소 후 재예약)
     */
    @Transactional(readOnly = false)
    public void updatePayment(Long userId, Long ottId, int delayDate) {
        Payment payment = paymentRepository.findTopByUserIdAndOttIdOrderByPaymentSubscribedAtDesc(userId, ottId)
            .orElseThrow(() -> new CustomException(PAYMENT_NOT_FOUND));

        try {
            reserveDelete(payment.getReserveId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Ott ott = ottRepository.findById(ottId)
            .orElseThrow(() -> new OttNotFoundException(ErrorCode.NOT_FOUND_OTT));

        PaymentReserve reserve = PaymentReserve.builder()
            .billingKey(payment.getBillingKey())
            .ott(ott)
            .user(payment.getUser())
            .paymentSubscribedAt(payment.getPaymentSubscribedAt().plusDays(delayDate))
            .build();

        try {
            reservePayment(reserve);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}