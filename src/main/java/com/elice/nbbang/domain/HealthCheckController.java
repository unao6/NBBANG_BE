package com.elice.nbbang.domain;

import java.util.Map;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthCheckController {

    @GetMapping("/hc")
    public ResponseEntity<?> healthCheck() {
        Map<String, String> responseData = new TreeMap<>();
        responseData.put("status", "UP124");

        return ResponseEntity.ok(responseData);
    }
}