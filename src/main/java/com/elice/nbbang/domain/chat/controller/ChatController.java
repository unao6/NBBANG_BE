package com.elice.nbbang.domain.chat.controller;

import com.elice.nbbang.domain.chat.dto.ChatResponse;
import com.elice.nbbang.domain.chat.dto.Message;
import com.elice.nbbang.domain.chat.service.ChatService;
import com.elice.nbbang.domain.notification.controller.NotificationController;
import com.elice.nbbang.domain.notification.dto.SmsRequest;
import com.elice.nbbang.domain.notification.provider.NotificationSmsProvider;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.service.UserService;
import com.elice.nbbang.global.util.UserUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@AllArgsConstructor
public class ChatController{

    private final ChatService chatService;
    private final UserUtil userUtil;
    private final UserService userService;
    private final NotificationSmsProvider notificationSmsProvider;

    @PostMapping("/start")
    public ResponseEntity<ChatResponse> startChat() {
        String userEmail = userUtil.getAuthenticatedUserEmail();
        User user = userService.findByEmail(userEmail);

        Long chatId = chatService.getOrCreateChat(user.getId());
        List<Message> messages = chatService.getChatMessages(chatId);

        SmsRequest smsRequest = new SmsRequest(
                user.getPhoneNumber(),
                "안녕하세요!\nOTT 계정 공유 플랫폼'N/BBANG'입니다."
        );
        notificationSmsProvider.sendSms(smsRequest);

        return ResponseEntity.ok(new ChatResponse(chatId, messages));
    }

    @DeleteMapping("/empty")
    public void deleteEmptyChat() {
        chatService.deleteEmptyChat();
    }

}
