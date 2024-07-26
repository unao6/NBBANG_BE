package com.elice.nbbang.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoPaySubscriptionResponse {
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

    @JsonProperty("payment_method_type")
    private String paymentMethodType;

    @JsonProperty("amount")
    private Amount amount;

    @JsonProperty("card_info")
    private CardInfo cardInfo;

    @JsonProperty("item_name")
    private String itemName;

    @JsonProperty("item_code")
    private String itemCode;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("approved_at")
    private LocalDateTime approvedAt;

    @JsonProperty("payload")
    private String payload;

    @Getter
    @Builder
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
    public static class CardInfo {
        @JsonProperty("purchase_corp")
        private String purchaseCorp;

        @JsonProperty("purchase_corp_code")
        private String purchaseCorpCode;

        @JsonProperty("issuer_corp")
        private String issuerCorp;

        @JsonProperty("issuer_corp_code")
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
    }
}
