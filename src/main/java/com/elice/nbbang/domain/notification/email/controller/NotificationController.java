package com.elice.nbbang.domain.notification.email.controller;

import com.elice.nbbang.domain.notification.email.provider.NotificationEmailProvider;
import com.elice.nbbang.domain.notification.email.dto.EmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationEmailProvider notificationEmailProvider;

    @PostMapping("/email")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest emailRequest) {
        boolean isSent = notificationEmailProvider.sendEmail(
                emailRequest.getEmail(),
                emailRequest.getSubject(),
                emailRequest.getMessage()
        );

        if (isSent) {
            return ResponseEntity.ok("이메일 발송 성공");
        } else {
            return ResponseEntity.status(500).body("이메일 발송 실패.");
        }
    }
}
