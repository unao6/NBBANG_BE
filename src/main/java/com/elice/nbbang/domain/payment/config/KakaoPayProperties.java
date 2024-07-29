package com.elice.nbbang.domain.payment.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter @Setter
@Component
@ConfigurationProperties(prefix = "kakao.pay")
public class KakaoPayProperties {
    private String secretKey;
    private String readyCreateUrl;
    private String readyCreateRedirectUrl;
    private String readyApproveUrl;
    private String readyFailUrl;
    private String readyCancelUrl;
    private String subscribeUrl;
    private String cancelUrl;
    private String sequenceCid;
    private String subscriptionCid;
    private String itemName;
    private Integer quantity;
    private Integer testAmount;
    private Integer totalAmount;
    private Integer taxFreeAmount;
    private String paymentMethodType;
}
