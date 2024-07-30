package com.elice.nbbang.domain.payment.dto;

import com.elice.nbbang.domain.payment.config.KakaoPayProperties;
import com.elice.nbbang.domain.payment.entity.Payment;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPaySubscriptionRequest {
    @JsonProperty("cid")
    private String cid;

    @JsonProperty("sid")
    private String sid;

    @JsonProperty("partner_order_id")
    private String partnerOrderId;

    @JsonProperty("partner_user_id")
    private String partnerUserId;

    @JsonProperty("item_name")
    private String itemName;

    @JsonProperty("item_code")
    private String itemCode;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("total_amount")
    private Integer totalAmount;

    @JsonProperty("tax_free_amount")
    private Integer taxFreeAmount;

    @JsonProperty("vat_amount")
    private Integer vatAmount;

    @JsonProperty("green_deposit")
    private Integer greenDeposit;

    @JsonProperty("payload")
    private String payload;

    public static KakaoPaySubscriptionRequest fromProperties(KakaoPayProperties properties, Payment payment) {
        return KakaoPaySubscriptionRequest.builder()
            .cid(properties.getSubscriptionCid())
            .sid(payment.getSid())
            .partnerOrderId(payment.getPartnerOrderId())
            .partnerUserId(payment.getPartnerUserId())
            .itemName(properties.getItemName())
            .quantity(properties.getQuantity())
            .totalAmount(properties.getTotalAmount())
            .taxFreeAmount(properties.getTaxFreeAmount())
            .build();
    }
}
