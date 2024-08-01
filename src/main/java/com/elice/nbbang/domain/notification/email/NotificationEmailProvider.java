package com.elice.nbbang.domain.notification.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEmailProvider {

    @Qualifier("notificationMailSender")
    public JavaMailSender notificationMailSender;

    @Value("${mail.notification.from}")
    private String fromAddress;

    @Value("${mail.notification.from.name}")
    private String fromName;


}
