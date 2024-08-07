package com.elice.nbbang.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "coolsms")
public class MessageProperties {
    private String apikey;
    private String apisecret;
    private String fromnumber;
}
