package com.elice.nbbang.domain.user.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEmailProvider {

    @Qualifier("userMailSender")
    private final JavaMailSender userMailSender;

    @Value("${mail.user.from}")
    private String fromAddress;

    @Value("${mail.user.from.name}")
    private String fromName;

    private final String SUBJECT = "[N/BBANG] 인증메일입니다.";

    public boolean sendCertificationMail(String email, String certificationNumber) {

        try {
            MimeMessage message = userMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

            String htmlContent = getCertificationMessage(certificationNumber);

            messageHelper.setTo(email);
            messageHelper.setSubject(SUBJECT);
            messageHelper.setText(htmlContent, true);
            messageHelper.setFrom(fromAddress, fromName); // 발신자 주소와 이름 설정

            userMailSender.send(message);

        } catch (Exception exception) {
            exception.printStackTrace();

            return false;
        }

        return true;
    }

    private String getCertificationMessage(String certificationNumber) {

        String certificationMessage = "";
        certificationMessage += "<h2>[N/BBANG] 인증메일</h2>";
        certificationMessage += "<h3>인증코드 : <strong style='font-size: 32px;" +
                "letter-spacing: 8px;'>" + certificationNumber + "</strong></h3>";

        return certificationMessage;
    }
}
