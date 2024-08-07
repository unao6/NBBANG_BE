package com.elice.nbbang.domain.chat.controller;

import com.elice.nbbang.domain.chat.dto.Message;
import com.elice.nbbang.domain.chat.dto.MessageRequest;
import com.elice.nbbang.domain.chat.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebSocketController {

    private final ChatService chatService;

    public WebSocketController(ChatService chatService) {
        this.chatService = chatService;
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
