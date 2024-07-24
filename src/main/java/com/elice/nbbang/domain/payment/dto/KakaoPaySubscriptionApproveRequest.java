package com.elice.nbbang.domain.payment.dto;

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
public class KakaoPaySubscriptionApproveRequest {
    private String cid;
    private String tid;
    private String partner_order_id;
    private String partner_user_id;
    private String pgToken;

    public String toFormUrlEncoded() throws UnsupportedEncodingException {
        return Stream.of(
                new AbstractMap.SimpleEntry<>("cid", cid),
                new AbstractMap.SimpleEntry<>("tid", tid),
                new AbstractMap.SimpleEntry<>("pg_token", pgToken)
            )
            .map(entry -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
            .collect(Collectors.joining("&"));
    }

}
