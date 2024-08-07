package com.elice.nbbang.domain.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class Message {
    private String nickname;
    private String text;
    private LocalDateTime sentAt;

    public Message(String nickname, String text, LocalDateTime sentAt) {
        this.nickname = nickname;
        this.text = text;
        this.sentAt = sentAt;
    }
}
