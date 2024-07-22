package com.elice.nbbang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class NbbangApplication {

	public static void main(String[] args) {
		SpringApplication.run(NbbangApplication.class, args);
	}

}
