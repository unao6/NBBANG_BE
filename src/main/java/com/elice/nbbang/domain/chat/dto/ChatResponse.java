package com.elice.nbbang.domain.chat.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChatResponse {
    private Long chatId;
    private List<Message> messages;

    public ChatResponse(Long chatId, List<Message> messages) {
        this.chatId = chatId;
        this.messages = messages;
    }
}