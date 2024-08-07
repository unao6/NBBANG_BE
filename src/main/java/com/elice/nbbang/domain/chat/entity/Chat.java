package com.elice.nbbang.domain.chat.entity;

import com.elice.nbbang.domain.chat.converter.MessageConverter;
import com.elice.nbbang.domain.chat.dto.Message;
import com.elice.nbbang.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "chat")
public class Chat {
    @Id
    @Column(name="chat_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Convert(converter = MessageConverter.class)
    @Column(columnDefinition = "json")
    private List<Message> messages = new ArrayList<>();

    private LocalDateTime lastRepliedAt;

    private LocalDateTime endedAt;

    @Column(nullable = false)
    private Boolean status;  // true: 상담 진행 중, false: 상담 종료
}
