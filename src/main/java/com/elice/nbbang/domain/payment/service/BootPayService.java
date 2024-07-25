package com.elice.nbbang.domain.payment.service;

import java.util.HashMap;
import kr.co.bootpay.Bootpay;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BootPayService {

    private Bootpay bootpay;

    public BootPayService(@Value("${bootpay.applicationId}") String applicationId,
                            @Value("${bootpay.privateKey}") String privateKey) {
        this.bootpay = new Bootpay(applicationId, privateKey);
    }

    //빌링키 조회 API
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
}