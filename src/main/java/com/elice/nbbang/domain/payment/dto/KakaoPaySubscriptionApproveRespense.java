package com.elice.nbbang.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoPaySubscriptionApproveRespense {
    @JsonProperty("aid")
    private final String aid;

    @JsonProperty("tid")
    private final String tid;

    @JsonProperty("cid")
    private final String cid;

    @JsonProperty("partner_order_id")
    private final String partnerOrderId;

    @JsonProperty("partner_user_id")
    private final String partnerUserId;

    @JsonProperty("payment_method_type")
    private final String paymentMethodType;

    @JsonProperty("amount")
    private final Amount amount;

    @JsonProperty("approved_at")
    private final String approvedAt;

    @Getter
    @Builder
    public static class Amount {
        @JsonProperty("total")
        private final int total;

        @JsonProperty("tax_free")
        private final int taxFree;

        @JsonProperty("vat")
        private final int vat;

        @JsonProperty("point")
        private final int point;

        @JsonProperty("discount")
        private final int discount;
    }

}
