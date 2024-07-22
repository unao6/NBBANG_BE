package com.elice.nbbang.domain.payment.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class KakaoPaySubscriptionApproveRequest {

    @NotNull
    private String cid;
    @NotNull
    private String tid;
    @NotNull
    private String partner_order_id;
    @NotNull
    private String partner_user_id;
}
