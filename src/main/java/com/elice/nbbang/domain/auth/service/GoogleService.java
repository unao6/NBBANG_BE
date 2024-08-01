package com.elice.nbbang.domain.auth.service;

import com.elice.nbbang.domain.auth.dto.GoogleUserInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GoogleService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String GOOGLE_TOKEN_INFO_URL = "https://oauth2.googleapis.com/tokeninfo?id_token=";

    public GoogleUserInfo verifyToken(String tokenId) {
        try {
            String url = GOOGLE_TOKEN_INFO_URL + tokenId;
            return restTemplate.getForObject(url, GoogleUserInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}