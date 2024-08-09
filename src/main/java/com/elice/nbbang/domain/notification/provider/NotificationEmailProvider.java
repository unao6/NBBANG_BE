package com.elice.nbbang.domain.notification.provider;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEmailProvider {

    @Qualifier("notificationMailSender")
    public final JavaMailSender notificationMailSender;

    @Value("${mail.notification.from}")
    private String fromAddress;

    @Value("${mail.notification.from.name}")
    private String fromName;

    public boolean sendEmail(String email, String subject, String message) {
        try {
            MimeMessage mimeMessage = notificationMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            messageHelper.setTo(email);
            messageHelper.setSubject(subject);
            messageHelper.setText(message, true);
            messageHelper.setFrom(fromAddress, fromName);

            notificationMailSender.send(mimeMessage);
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
        return true;
    }

}
