package com.elice.nbbang.domain.payment.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import kr.co.bootpay.Bootpay;
import kr.co.bootpay.model.request.Cancel;
import kr.co.bootpay.model.request.SubscribePayload;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BootPayService {

    private static final String ORDER_NAME = "엔빵 예약결제";
    private Bootpay bootpay;

    public BootPayService(@Value("${bootpay.applicationId}") String applicationId,
        @Value("${bootpay.privateKey}") String privateKey) {
        this.bootpay = new Bootpay(applicationId, privateKey);
    }

    //빌링키 조회
    public String getBillingKey(String receiptId) throws Exception {
        bootpay.getAccessToken();

        try {
            HashMap<String, Object> res = bootpay.lookupBillingKey(receiptId);
            JSONObject json = new JSONObject(res);
            System.out.printf("JSON: %s", json);

            if (res.get("error_code") == null) {
                System.out.println("getKey success: " + res);
            } else {
                System.out.println("getKey false: " + res);
            }

            return res.get("billing_key").toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("에러 발생");
        }
    }

    //결제 예약
    public String reservePayment(String billingKey, Integer amount, LocalDateTime paymentTime) throws Exception {
        bootpay.getAccessToken();

        SubscribePayload payload = new SubscribePayload();
        payload.billingKey = billingKey;
        payload.orderName = ORDER_NAME;
        payload.price = amount;
        payload.orderId = "" + (System.currentTimeMillis() / 1000);

        Date date = Date.from(paymentTime.atZone(ZoneId.of("UTC")).minusHours(9).toInstant());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        payload.reserveExecuteAt = sdf.format(date);

        try {
            HashMap<String, Object> res = bootpay.reserveSubscribe(payload);
            JSONObject json = new JSONObject(res);
            System.out.printf("JSON: %s", json);

            if (res.get("error_code") == null) {
                System.out.println("reserveSubscribe success: " + res);
            } else {
                System.out.println("reserveSubscribe false: " + res);
            }

            return res.get("reserve_id").toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("에러 발생");
        }
    }

    //예약 결제 조회
    public HashMap<String, Object> reserveLookup(String reserveId) throws Exception {
        bootpay.getAccessToken();

        try {
            HashMap<String, Object> res = bootpay.reserveSubscribeLookup(reserveId);
            JSONObject json = new JSONObject(res);
            System.out.printf("JSON: %s", json);

            if (res.get("error_code") == null) {
                System.out.println("getReceipt success: " + res);
            } else {
                System.out.println("getReceipt false: " + res);
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

    //예약 결제 취소
    public void reserveDelete(String reserveId) throws Exception {
        bootpay.getAccessToken();

        try {
            HashMap<String, Object> res = bootpay.reserveCancelSubscribe(reserveId);
            JSONObject json = new JSONObject(res);
            System.out.printf("JSON: %s", json);

            if (res.get("error_code") == null) {
                System.out.println("getReceipt success: " + res);
            } else {
                System.out.println("getReceipt false: " + res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //완료된 결제 취소
    public void cancelPayment(String id, Double cancelAmount) throws Exception {
        bootpay.getAccessToken();

        try {
            Cancel cancel = new Cancel();
            cancel.receiptId = id;
            cancel.cancelPrice = cancelAmount;

            HashMap<String, Object> res = bootpay.receiptCancel(cancel);

            if (res.get("error_code") == null) {
                System.out.println("receiptCancel success: " + res);
            } else {
                System.out.println("receiptCancel false: " + res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}