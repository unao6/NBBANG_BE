package com.elice.nbbang.domain.notification.controller;

import com.elice.nbbang.domain.notification.dto.SmsRequest;
import com.elice.nbbang.domain.notification.provider.NotificationEmailProvider;
import com.elice.nbbang.domain.notification.dto.EmailRequest;
import com.elice.nbbang.domain.notification.provider.NotificationSmsProvider;
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
    private final NotificationSmsProvider notificationSmsProvider;

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

    @PostMapping("/sms")
    public ResponseEntity<String> sendSms(@RequestBody SmsRequest smsRequest) {
        String result = notificationSmsProvider.sendSms(smsRequest);

        if (result.equals("SMS 발송 성공")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(500).body(result);
        }
    }


}
