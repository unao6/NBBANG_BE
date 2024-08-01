package com.elice.nbbang.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class GoogleUserInfo {

    private String sub;
    private String email;
    private String name;
    private String picture;

    public GoogleUserInfo(Map<String, Object> attributes) {
        this.sub = (String) attributes.get("sub");
        this.email = (String) attributes.get("email");
        this.name = (String) attributes.get("name");
        this.picture = (String) attributes.get("picture");
    }

}