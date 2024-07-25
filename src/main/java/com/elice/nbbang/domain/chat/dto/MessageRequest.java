package com.elice.nbbang.domain.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequest {
    private Long chatId;
    private Message message;
}
