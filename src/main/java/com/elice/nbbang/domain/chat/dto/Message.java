package com.elice.nbbang.domain.chat.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Message {
    private Long UserId;
    private String nickname;
    private String text;
    private LocalDateTime sentAt;
}
