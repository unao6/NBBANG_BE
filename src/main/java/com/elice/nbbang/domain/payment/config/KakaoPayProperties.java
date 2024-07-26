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
    private String cancelUrl;
    private String cid;
    private String itemName;
    private int quantity;
    private Long totalAmount;
    private Long taxFreeAmount;
    private String paymentMethodType;
}
