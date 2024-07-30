package com.elice.nbbang.domain.chat.service;

import com.elice.nbbang.domain.chat.dto.Message;
import com.elice.nbbang.domain.chat.entity.Chat;
import com.elice.nbbang.domain.chat.repository.ArchivedChatRepository;
import com.elice.nbbang.domain.chat.repository.ChatRepository;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.repository.UserRepository;
import com.elice.nbbang.domain.chat.exception.UserNotFoundException;
import com.elice.nbbang.domain.chat.exception.ChatNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@DisplayName("ChatService 테스트")
class ChatServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private ArchivedChatRepository archivedChatRepository;

    @InjectMocks
    private ChatService chatService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    @DisplayName("상담시작 - 기존 상담 존재")
    void testStartChat_ExcistingChat() {
        Long userId =1L;
        Chat chat = new Chat();
        chat.setId(1L);

        when(chatRepository.findByUserIdAndStatus(userId, true)).thenReturn(Optional.of(chat));
        Long chatId = chatService.startChat(userId);

        assertEquals(1L, chatId);
    }

    @Test
    @DisplayName("상담시작 - 새로운 상담")
    void testStartChat_NewChat() {
        Long userId =1L;

        when(chatRepository.findByUserIdAndStatus(userId, false)).thenReturn(Optional.empty());
        Long chatId = chatService.startChat(userId);

        assertEquals(-1L, chatId);
    }

    @Test
    @DisplayName("새로운 채팅방에서 메시지 전송")
    void testSendMessage_NewChat() {
    }

    @Test
    void endChat() {
    }

    @Test
    void getAllChats() {
    }

    @Test
    void getChatsByUserId() {
    }

    @Test
    void getChatMessages() {
    }

    @Test
    void archiveChat() {
    }

    @Test
    void getArchivedChats() {
    }

    @Test
    void getArchivedChat() {
    }

    @Test
    void endChatsAfter24Hours() {
    }

    @Test
    void deleteOldChats() {
    }
}