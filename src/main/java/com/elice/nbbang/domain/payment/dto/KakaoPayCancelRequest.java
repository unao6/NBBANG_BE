package com.elice.nbbang.domain.payment.dto;

import com.elice.nbbang.domain.payment.config.KakaoPayProperties;
import com.elice.nbbang.domain.payment.entity.Payment;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPayCancelRequest {
    @JsonProperty("cid")
    private String cid;

    @JsonProperty("tid")
    private String tid;

    @JsonProperty("cancel_amount")
    private Integer cancelAmount;

    @JsonProperty("cancel_tax_free_amount")
    private Integer cancelTaxFreeAmount;

    //todo: 더 정확한 취소 요청을 할때 이용. 디테일한 정책 필요.
//    @JsonProperty("cancel_vat_amount")
//    private Integer cancelVatAmount;

//    @JsonProperty("cancel_available_amount")
//    private Integer cancelAvailableAmount;

    @JsonProperty("payload")
    private String payload;

    public static KakaoPayCancelRequest fromProperties(KakaoPayProperties properties, Payment payment, Integer cancelAmount, Integer cancelTaxFreeAmount, String payload) {
        return KakaoPayCancelRequest.builder()
            .cid(properties.getSubscriptionCid())
            .tid(payment.getTid())
            .cancelAmount(cancelAmount)
            .cancelTaxFreeAmount(cancelTaxFreeAmount)
            .payload(payload)
            .build();
    }

}
