package com.elice.nbbang.domain.payment.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class KakaoPaySubscriptionRequest {

    @NotNull
    private String cid;
    @NotNull
    private String sid;
    @NotNull
    private String partner_order_id;
    @NotNull
    private String partner_user_id;
    @NotNull
    private String item_name;
    @NotNull
    private String quantity;
    @NotNull
    private String total_amount;
}
