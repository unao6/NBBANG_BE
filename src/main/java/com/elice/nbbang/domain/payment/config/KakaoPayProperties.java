package com.elice.nbbang.domain.payment.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter @Setter
@Component
@ConfigurationProperties(prefix = "kakao.pay")
public class KakaoPayProperties {
    private String apiKey;
    private String subscriptionCreateUrl;
    private String subscriptionApprovalUrl;
    private String subscriptionFailUrl;
    private String subscriptionCancelUrl;
    private String cid;
    private String itemName;
    private int quantity;
    private Long amount;
    private Long taxFreeAmount;
}
