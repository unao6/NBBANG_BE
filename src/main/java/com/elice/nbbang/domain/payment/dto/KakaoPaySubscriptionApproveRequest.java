package com.elice.nbbang.domain.payment.dto;

import com.elice.nbbang.domain.payment.config.KakaoPayProperties;
import com.elice.nbbang.domain.payment.entity.Payment;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoPaySubscriptionApproveRequest {
    @JsonProperty("cid")
    private String cid;

    @JsonProperty("tid")
    private String tid;

    @JsonProperty("partner_order_id")
    private String partnerOrderId;

    @JsonProperty("partner_user_id")
    private String partnerUserId;

    @JsonProperty("pg_token") //여기 왜 이름이 이렇게 되어야 매칭이되지-_-;;;
    private String pgToken;

    public static KakaoPaySubscriptionApproveRequest fromProperties(KakaoPayProperties properties,
        Payment payment, String pgToken) {
        return KakaoPaySubscriptionApproveRequest.builder()
            .cid(properties.getSubscriptionCid())
            .tid(payment.getTid())
            .partnerOrderId(payment.getPartnerOrderId())
            .partnerUserId(payment.getPartnerUserId())
            .pgToken(pgToken)
            .build();
    }
}
