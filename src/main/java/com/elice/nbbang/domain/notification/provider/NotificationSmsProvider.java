package com.elice.nbbang.domain.notification.provider;

import com.elice.nbbang.domain.notification.dto.SmsRequest;
import com.elice.nbbang.global.config.MessageProperties;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationSmsProvider {

    private final MessageProperties messageProperties;
    private DefaultMessageService messageService;


    // 초기화 블록을 이용하여 messageService 초기화
    public void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(
                messageProperties.getApikey(),
                messageProperties.getApisecret(),
                "https://api.coolsms.co.kr"
        );
    }

    public String sendSms(SmsRequest smsRequest) {
        if (this.messageService == null) {
            init();
        }

        Message message = new Message();
        message.setFrom(messageProperties.getFromnumber());
        message.setTo(smsRequest.getPhoneNumber());
        message.setText(smsRequest.getMessage());

        try {
            messageService.send(message);
            return "SMS 발송 성공";
        } catch (NurigoMessageNotReceivedException exception) {
            System.out.println(exception.getFailedMessageList());
            System.out.println(exception.getMessage());
            return "SMS 발송 실패: " + exception.getMessage();
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return "SMS 발송 실패: " + exception.getMessage();
        }
    }
}
