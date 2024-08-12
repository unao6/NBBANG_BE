package com.elice.nbbang.domain.chat.controller;

import com.elice.nbbang.domain.chat.dto.Message;
import com.elice.nbbang.domain.chat.dto.MessageRequest;
import com.elice.nbbang.domain.chat.service.ChatService;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.service.UserService;
import com.elice.nbbang.global.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    private final ChatService chatService;
    private final JWTUtil jwtUtil;
    private final UserService userService;

    @MessageMapping("/auth")
    public void authenticate(@Payload String token, SimpMessageHeaderAccessor headerAccessor) {
        try {
            logger.debug("Received token: {}", token);
            String email = jwtUtil.getEmail(token);
            User user = userService.findByEmail(email);
            logger.info("User authenticated: {}", user.getEmail());

            headerAccessor.getSessionAttributes().put("user", user);

        } catch (Exception e) {
            logger.error("Error during authentication", e);
        }
    }

    @MessageMapping("/chat/send/{chatId}")
    @SendTo("/topic/messages/{chatId}")
    public Message sendMessage(MessageRequest messageRequest) {
        Long chatId = messageRequest.getChatId();
        Message message = messageRequest.getMessage();

        chatService.sendMessage(chatId, message);

        return message;
    }
}
