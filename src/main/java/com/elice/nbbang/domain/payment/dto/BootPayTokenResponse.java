package com.elice.nbbang.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) //JSON 문자열에 클래스에 정의되지 않은 필드가 포함되어 있어도 무시
public class BootPayTokenResponse {

    @JsonProperty("expire_in")
    private Long expireIn;

    @JsonProperty("access_token")
    private String accessToken;
}