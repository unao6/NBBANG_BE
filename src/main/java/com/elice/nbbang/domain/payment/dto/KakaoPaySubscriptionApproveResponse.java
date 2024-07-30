package com.elice.nbbang.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoPaySubscriptionApproveResponse {
    @JsonProperty("aid")
    private String aid;

    @JsonProperty("tid")
    private String tid;

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

    @JsonProperty("payload")
    private String payload;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("amount")
    private Amount amount;

    @JsonProperty("payment_method_type")
    private String paymentMethodType;

    @JsonProperty("card_info")
    private CardInfo cardInfo;

    @JsonProperty("sequential_payment_methods")
    private List<SequentialPaymentMethod> sequentialPaymentMethods;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("approved_at")
    private LocalDateTime approvedAt;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Amount {
        @JsonProperty("total")
        private Integer total;

        @JsonProperty("tax_free")
        private Integer taxFree;

        @JsonProperty("vat")
        private Integer vat;

        @JsonProperty("point")
        private Integer point;

        @JsonProperty("discount")
        private Integer discount;

        @JsonProperty("green_deposit")
        private Integer greenDeposit;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardInfo {
        @JsonProperty("kakaopay_purchase_corp")
        private String purchaseCorp;

        @JsonProperty("kakaopay_purchase_corp_code")
        private String purchaseCorpCode;

        @JsonProperty("kakaopay_issuer_corp")
        private String issuerCorp;

        @JsonProperty("kakaopay_issuer_corp_code")
        private String issuerCorpCode;

        @JsonProperty("bin")
        private String bin;

        @JsonProperty("card_type")
        private String cardType;

        @JsonProperty("install_month")
        private String installMonth;

        @JsonProperty("approved_id")
        private String approvedId;

        @JsonProperty("card_mid")
        private String cardMid;

        @JsonProperty("interest_free_install")
        private String interestFreeInstall;

        @JsonProperty("card_item_code")
        private String cardItemCode;

        @JsonProperty("installment_type")
        private String installmentType;
    }

    @Getter
    public static class SequentialPaymentMethod {
        @JsonProperty("payment_priority")
        private int paymentPriority;
        @JsonProperty("sid")
        private String sid;
        @JsonProperty("payment_method_type")
        private String paymentMethodType;
        @JsonProperty("card_info")
        private CardInfo cardInfo;
    }
}
