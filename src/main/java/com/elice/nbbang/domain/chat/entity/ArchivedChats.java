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
public class ArchivedChats {
    @Id
    @Column(name="archived_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

//    @Column(nullable = false)
//    private Long chatId;

    @Convert(converter = MessageConverter.class)
    @Column(nullable = false, columnDefinition = "json")
    private List<Message> messages = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime endedAt;

    @Column(nullable = false)
    private LocalDateTime savedAt;

    private String memo;
}
