package com.elice.nbbang.domain.chat.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ArchivedChatDto {
    private Long archivedChatId;
    private List<Message> messages;

    public ArchivedChatDto(Long archivedChatId, List<Message> messages) {
        this.archivedChatId = archivedChatId;
        this.messages = messages;
    }
}
