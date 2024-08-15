package com.elice.nbbang.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://3.39.127.36:3000")
                .allowedMethods("OPTIONS", "GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true);


    }
}

