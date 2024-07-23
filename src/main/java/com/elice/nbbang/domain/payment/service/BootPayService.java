package com.elice.nbbang.domain.payment.service;

import com.elice.nbbang.domain.payment.dto.BootPayTokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import kr.co.bootpay.Bootpay;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BootPayService {

    private Bootpay bootpay;

    /*
    * bootpay 개발문서 참조
    * */
    public BootPayService(@Value("${bootpay.applicationId}") String applicationId,
                            @Value("${bootpay.privateKey}") String privateKey) {
        this.bootpay = new Bootpay(applicationId, privateKey);
    }

    //부트페이 Access Token 발급
    public BootPayTokenResponse getAccessToken() throws Exception {
        HashMap<String, Object> res = bootpay.getAccessToken();
        ObjectMapper mapper = new ObjectMapper();
        if (res.get("error_code") == null) {    //성공
            System.out.println("goGetToken success: " + res);
            return mapper.convertValue(res, BootPayTokenResponse.class);
        } else {
            throw new Exception("goGetToken false: " + res);
        }
    }
}