package com.elice.nbbang.domain.notification.scheduler;

import com.elice.nbbang.domain.notification.dto.EmailRequest;
import com.elice.nbbang.domain.notification.dto.SmsRequest;
import com.elice.nbbang.domain.notification.provider.NotificationEmailProvider;
import com.elice.nbbang.domain.notification.provider.NotificationSmsProvider;
import com.elice.nbbang.domain.party.entity.PartyMember;
import com.elice.nbbang.domain.party.repository.PartyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Component
@RequiredArgsConstructor
public class ExpirationNotificationScheduler {

    private final PartyMemberRepository partyMemberRepository;
    private final NotificationSmsProvider notificationSmsProvider;
    private final NotificationEmailProvider notificationEmailProvider;


    // 결제 예정일 알림
    @Scheduled(cron = "0 0 12 * * ?") // 매일 정오에 실행
    public void checkAndSendExpirationNotifications() {
        LocalDate targetDate = LocalDate.now().plusDays(3);
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.atTime(LocalTime.MAX);

        List<PartyMember> members = partyMemberRepository.findPartyMembersByExpirationDate(startOfDay, endOfDay);

        for (PartyMember member : members) {
            String ottName = member.getOtt().getName();
            String userNickname = member.getUser().getNickname();
            String userPhoneNumber = member.getUser().getPhoneNumber();
            String userEmail = member.getUser().getEmail();
            LocalDate expirationDate = member.getExpirationDate().toLocalDate();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM월 dd일");
            String formattedDate = expirationDate.format(formatter);

            // SMS
            String smsMessage = String.format(
                    "[N/BBANG]\n" +
                    "%s의 다음달 결제가 3일 후(%s) 진행될 예정입니다.\n",
                    ottName, formattedDate
            );
            SmsRequest smsRequest = new SmsRequest(userPhoneNumber, smsMessage);
            notificationSmsProvider.sendSms(smsRequest);

            // 이메일
            String emailSubject = "N/BBANG 결제 예정일 알림";
            String emailMessage = String.format(
                    "<html>" +
                    "<body>" +
                    "<h3>안녕하세요, OTT 계정 공유 플랫폼 N/BBANG입니다.</h3>" +
                    "<p>%s님이 현재 이용 중이신 %s의 이용 가능일이 3일 남았습니다.<p><br>" +
                    "<p>%s일에 재결제가 진행될 예정이며, 이용 가능일이 자동으로 연장됩니다. </p><br>" +
                    "<p>이용을 원치 않으실 경우 홈페이지에서 파티를 탈퇴하실 수 있습니다. </p><br>" +
                    "<p>감사합니다.</p>" +
                    "</body>" +
                    "</html>",
                    userNickname, ottName, formattedDate
                    );
            EmailRequest emailRequest = new EmailRequest(userEmail, emailSubject, emailMessage);
            notificationEmailProvider.sendEmail(emailRequest);
        }
    }
}
