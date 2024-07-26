package com.elice.nbbang.domain.payment.dto;

import com.elice.nbbang.domain.payment.config.KakaoPayProperties;
import com.elice.nbbang.domain.payment.entity.Payment;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoPayCancelRequest {
    @JsonProperty("cid")
    private String cid;

    @JsonProperty("tid")
    private String tid;

    @JsonProperty("cancel_amount")
    private Integer cancelAmount;

    @JsonProperty("cancel_tax_free_amount")
    private Integer cancelTaxFreeAmount;

    @JsonProperty("cancel_vat_amount")
    private Integer cancelVatAmount;

    @JsonProperty("cancel_available_amount")
    private Integer cancelAvailableAmount;

    @JsonProperty("payload")
    private String payload;

    public static KakaoPayCancelRequest fromProperties(KakaoPayProperties properties, Payment payment, Integer cancelAmount, Integer cancelTaxFreeAmount, Integer cancelVatAmount, Integer cancelAvailableAmount, String payload) {
        return KakaoPayCancelRequest.builder()
            .cid(properties.getSubscriptionCid())
            .tid(payment.getTid())
            .cancelAmount(cancelAmount)
            .cancelTaxFreeAmount(cancelTaxFreeAmount)
            .cancelVatAmount(cancelVatAmount)
            .cancelAvailableAmount(cancelAvailableAmount)
            .payload(payload)
            .build();
    }

}
