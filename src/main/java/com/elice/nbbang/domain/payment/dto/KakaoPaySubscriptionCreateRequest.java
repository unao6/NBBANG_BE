package com.elice.nbbang.domain.payment.dto;

import com.elice.nbbang.domain.payment.config.KakaoPayProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
public class KakaoPaySubscriptionCreateRequest {

    @JsonProperty("cid")
    private String cid;

    @JsonProperty("partner_order_id")
    private String partnerOrderId;

    @JsonProperty("partner_user_id")
    private String partnerUserId;

    @JsonProperty("item_name")
    private String itemName;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("total_amount")
    private Integer totalAmount;

    @JsonProperty("tax_free_amount")
    private Integer taxFreeAmount;

    @JsonProperty("approval_url")
    private String approveUrl;

    @JsonProperty("fail_url")
    private String failUrl;

    @JsonProperty("cancel_url")
    private String cancelUrl;

    @JsonProperty("payment_method_type")
    private String paymentMethodType;

    public static KakaoPaySubscriptionCreateRequest fromProperties(KakaoPayProperties properties, String partnerOrderId, String partnerUserId) {
        return KakaoPaySubscriptionCreateRequest.builder()
            .cid(properties.getSubscriptionCid())
            .partnerOrderId(partnerOrderId)
            .partnerUserId(partnerUserId)
            .paymentMethodType(properties.getPaymentMethodType())
            .itemName(properties.getItemName())
            .quantity(properties.getQuantity())
            .totalAmount(properties.getTotalAmount())  //**아주 에러때문에 개고생을 했음. 프로퍼티스의 값은 Long이었음.. 그래서 intValue()로 형변환을 해줘야했는데 바꿈 Integer로
            .taxFreeAmount(properties.getTaxFreeAmount())
            .approveUrl(properties.getReadyCreateRedirectUrl())
            .failUrl(properties.getReadyFailUrl())
            .cancelUrl(properties.getReadyCancelUrl())
            .build();
    }
}
