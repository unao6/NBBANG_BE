package com.elice.nbbang;

import com.elice.nbbang.domain.payment.config.KakaoPayProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EnableConfigurationProperties(KakaoPayProperties.class)
@SpringBootApplication
public class NbbangApplication {

	public static void main(String[] args) {
		SpringApplication.run(NbbangApplication.class, args);
	}

}
