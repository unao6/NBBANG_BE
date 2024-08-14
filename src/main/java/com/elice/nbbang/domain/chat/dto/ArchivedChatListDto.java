package com.elice.nbbang.domain.chat.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ArchivedChatListDto {

    private Long id;
    private String memo;
    private List<Message> messages;
    private LocalDateTime endedAt;
    private LocalDateTime savedAt;

    public ArchivedChatListDto(Long id, String memo, List<Message> messages, LocalDateTime endedAt, LocalDateTime savedAt) {
        this.id = id;
        this.memo = memo;
        this.messages = messages;
        this.endedAt = endedAt;
        this.savedAt = savedAt;
    }
}
