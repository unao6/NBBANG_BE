package com.elice.nbbang.domain.payment.paymentEmailProvider;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEmailProvider {


    @Qualifier("paymentMailSender")
    private final JavaMailSender paymentMailSender;


    public String getCancelPaymentBody(String userName, int cancelAmount) {
        return "<h2>N/BBANG을 이용해주셔서 감사합니다.</h2>" +
            "<p>" + userName + "님, 요청하신 결제가 취소되었습니다.</p>" +
            "<p>취소 금액: " + cancelAmount + "원</p>" +
            "<p>궁금한 사항이 있으시면 언제든지 문의해 주세요.</p>" +
            "<p>감사합니다.</p>";
    }

    public boolean sendCancelPaymentEmail(String userEmail, String userNickName, int cancelAmount) {
        try {
            MimeMessage mimeMessage = paymentMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            String subject = "N/BBANG 결제 취소 안내";
            String body = getCancelPaymentBody(userNickName, cancelAmount);

            messageHelper.setTo(userEmail);
            messageHelper.setSubject(subject);
            messageHelper.setText(body, true);
            messageHelper.setFrom("no-reply@nbbang.com", "N/BBANG");

            paymentMailSender.send(mimeMessage);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getCompletePaymentBody(String userNickName, double paymentAmount) {
        return "<h2>N/BBANG을 이용해주셔서 감사합니다.</h2>" +
            "<p>" + userNickName + "님, 요청하신 결제가 완료되었습니다.</p>" +
            "<p>결제 금액: " + paymentAmount + "원</p>" +
            "<p>감사합니다.</p>";
    }

    public boolean sendCompletePaymentEmail(String userEmail, String userNickName, int paymentAmount) {
        try {
            MimeMessage mimeMessage = paymentMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            String subject = "N/BBANG 결제 완료 안내";
            String body = getCompletePaymentBody(userNickName, paymentAmount);

            messageHelper.setTo(userEmail);
            messageHelper.setSubject(subject);
            messageHelper.setText(body, true);
            messageHelper.setFrom("no-reply@nbbang.com", "N/BBANG");

            paymentMailSender.send(mimeMessage);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
