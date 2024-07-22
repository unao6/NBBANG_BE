package com.elice.nbbang.domain.payment.service;

import com.elice.nbbang.domain.payment.dto.KakaoPaySubscriptionApproveRequest;
import com.elice.nbbang.domain.payment.dto.KakaoPaySubscriptionRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KakaoPayService {

    @Value("${kakao-pay.api-key}")
    private String apiKey;

    public String requestSubscription(KakaoPaySubscriptionRequest request) throws Exception {
        String url = "https://kapi.kakao.com/v1/payment/subscription";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader("Authorization", "KakaoAK " + apiKey);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        StringEntity entity = new StringEntity(buildSubscriptionRequestParams(request));
        httpPost.setEntity(entity);

        var response = client.execute(httpPost);
        String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");

        client.close();
        return responseBody;
    }

    public String approveSubscription(KakaoPaySubscriptionApproveRequest request) throws Exception {
        String url = "https://kapi.kakao.com/v1/payment/subscription/approve";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader("Authorization", "KakaoAK " + apiKey);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        StringEntity entity = new StringEntity(buildApproveRequestParams(request));
        httpPost.setEntity(entity);

        var response = client.execute(httpPost);
        String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");

        client.close();
        return responseBody;
    }

    private String buildSubscriptionRequestParams(KakaoPaySubscriptionRequest request) {
        return "cid=" + request.getCid()
            + "&sid=" + request.getSid()
            + "&partner_order_id=" + request.getPartner_order_id()
            + "&partner_user_id=" + request.getPartner_user_id()
            + "&item_name=" + request.getItem_name()
            + "&quantity=" + request.getQuantity()
            + "&total_amount=" + request.getTotal_amount()
            + "&tax_free_amount=" + request.getTax_free_amount();
    }

    private String buildApproveRequestParams(KakaoPaySubscriptionApproveRequest request) {
        return "cid=" + request.getCid()
            + "&tid=" + request.getTid()
            + "&partner_order_id=" + request.getPartner_order_id()
            + "&partner_user_id=" + request.getPartner_user_id();
    }

}
