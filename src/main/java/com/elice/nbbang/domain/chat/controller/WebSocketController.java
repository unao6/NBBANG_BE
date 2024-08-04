package com.elice.nbbang.domain.chat.controller;

import com.elice.nbbang.domain.chat.dto.Message;
import com.elice.nbbang.domain.chat.dto.MessageRequest;
import com.elice.nbbang.domain.chat.service.ChatService;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.service.UserService;
import com.elice.nbbang.global.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final ChatService chatService;
    private final JWTUtil jwtUtil;
    private final UserService userService;

    @MessageMapping("/auth")
    public void authenticate(@Payload String token, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String email = jwtUtil.getEmail(token);
            User user = userService.findByEmail(email);

            if (user != null) {
                // 세션에 사용자 정보 저장
                headerAccessor.getSessionAttributes().put("user", user);
                System.out.println("User authenticated: " + user.getEmail());
            } else {
                System.out.println("Invalid user for token");
                // 연결 끊기 또는 에러 처리
            }
        } catch (Exception e) {
            System.out.println("Error during authentication: " + e.getMessage());
            // 연결 끊기 또는 에러 처리
        }
    }

    @MessageMapping("/chat/send")
    @SendTo("/queue/messages")
    public Message sendMessage(MessageRequest messageRequest) {
        Long chatId = messageRequest.getChatId();
        Long userId = messageRequest.getUserId();
        Message message = messageRequest.getMessage();
        if(chatId == -1L) {
            chatId = chatService.startChat(userId);
            messageRequest.setChatId(chatId);
        }
        chatService.sendMessage(chatId, userId, message);
        return message;
    }

}
