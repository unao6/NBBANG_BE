package com.elice.nbbang.domain.payment.dto;

import com.elice.nbbang.domain.payment.config.KakaoPayProperties;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoPaySubscriptionCreateRequest {
    //카멜케이스 써야함.
    private String cid;
    private String partner_order_id;
    private String partner_user_id;
    private String item_name;
    private int quantity;
    private Long total_amount;
    private Long tax_free_amount;
    private String approval_url;
    private String fail_url;
    private String cancel_url;

    public static KakaoPaySubscriptionCreateRequest fromProperties(KakaoPayProperties properties, String partnerOrderId, String partnerUserId) {
        return KakaoPaySubscriptionCreateRequest.builder()
            .cid(properties.getCid())
            .partner_order_id(partnerOrderId)
            .partner_user_id(partnerUserId)
            .item_name(properties.getItemName())
            .quantity(properties.getQuantity())
            .total_amount(properties.getAmount())
            .tax_free_amount(properties.getTaxFreeAmount())
            .approval_url(properties.getSubscriptionApprovalUrl())
            .fail_url(properties.getSubscriptionFailUrl())
            .cancel_url(properties.getSubscriptionCancelUrl())
            .build();
    }
    public String toFormUrlEncoded() throws UnsupportedEncodingException {
        return Stream.of(
                new AbstractMap.SimpleEntry<>("cid", cid),
                new AbstractMap.SimpleEntry<>("partner_order_id", partner_order_id),
                new AbstractMap.SimpleEntry<>("partner_user_id", partner_user_id),
                new AbstractMap.SimpleEntry<>("item_name", item_name),
                new AbstractMap.SimpleEntry<>("quantity", String.valueOf(quantity)),
                new AbstractMap.SimpleEntry<>("total_amount", String.valueOf(total_amount)),
                new AbstractMap.SimpleEntry<>("tax_free_amount", String.valueOf(tax_free_amount)),
                new AbstractMap.SimpleEntry<>("approval_url", approval_url),
                new AbstractMap.SimpleEntry<>("fail_url", fail_url),
                new AbstractMap.SimpleEntry<>("cancel_url", cancel_url)
            )
            .map(entry -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
            .collect(Collectors.joining("&"));
    }
}
