package com.elice.nbbang.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoPayCancelResponse {
    private String aid;
    private String tid;
    private String cid;
    private String status;

    @JsonProperty("partner_order_id")
    private String partnerOrderId;

    @JsonProperty("partner_user_id")
    private String partnerUserId;

    @JsonProperty("payment_method_type")
    private String paymentMethodType;

    private Amount amount;

    @JsonProperty("approved_cancel_amount")
    private ApprovedCancelAmount approvedCancelAmount;

    @JsonProperty("canceled_amount")
    private CanceledAmount canceledAmount;

    @JsonProperty("cancel_available_amount")
    private CancelAvailableAmount cancelAvailableAmount;

    @JsonProperty("item_name")
    private String itemName;

    @JsonProperty("item_code")
    private String itemCode;

    private Integer quantity;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("approved_at")
    private LocalDateTime approvedAt;

    @JsonProperty("canceled_at")
    private LocalDateTime canceledAt;

    private String payload;


    @Getter
    @Builder
    public static class Amount {
        private Long total;
        @JsonProperty("tax_free")
        private Long taxFree;
        private Long vat;
        private Long point;
        private Long discount;
        @JsonProperty("green_deposit")
        private Long greenDeposit;
    }

    @Getter
    @Builder
    public static class ApprovedCancelAmount {
        private Long total;
        @JsonProperty("tax_free")
        private Long taxFree;
        private Long vat;
        private Long point;
        private Long discount;
        @JsonProperty("green_deposit")
        private Long greenDeposit;
    }

    @Getter
    @Builder
    public static class CanceledAmount {
        private Long total;
        @JsonProperty("tax_free")
        private Long taxFree;
        private Long vat;
        private Long point;
        private Long discount;
        @JsonProperty("green_deposit")
        private Long greenDeposit;
    }

    @Getter
    @Builder
    public static class CancelAvailableAmount {
        private Long total;
        @JsonProperty("tax_free")
        private Long taxFree;
        private Long vat;
        private Long point;
        private Long discount;
        @JsonProperty("green_deposit")
        private Long greenDeposit;
    }
}
