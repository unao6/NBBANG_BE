package com.elice.nbbang.domain.chat.dto;

import com.elice.nbbang.domain.chat.entity.Chat;
import com.elice.nbbang.domain.chat.dto.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatListResponse {
    private Long id;
    private String nickname;
    private String lastMessage;
    private LocalDateTime lastRepliedAt;
    private Boolean status;

    public ChatListResponse(Chat chat) {
        this.id = chat.getId();
        this.nickname = chat.getUser().getNickname();
        this.lastRepliedAt = chat.getLastRepliedAt();
        this.status = chat.getStatus();

        // 마지막 메세지 가져오기
        List<Message> messages = chat.getMessages();
        if (messages != null && !messages.isEmpty()) {
            Message lastMessageObj = messages.get(messages.size() - 1); // 마지막 메시지
            this.lastMessage = String.format("%s: %s", lastMessageObj.getNickname(), lastMessageObj.getText());
        } else {
            this.lastMessage = "No messages available";
        }
    }
}