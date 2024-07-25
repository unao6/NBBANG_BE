package com.elice.nbbang.domain.chat.controller;

import com.elice.nbbang.domain.chat.dto.Message;
import com.elice.nbbang.domain.chat.dto.MessageRequest;
import com.elice.nbbang.domain.chat.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

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
        Message message = messageRequest.getMessage();
        if(chatId == -1L) {
            chatId = chatService.startChat(message.getUserId());
            messageRequest.setChatId(chatId);
        }
        chatService.sendMessage(chatId, message);
        return message;
    }

}
