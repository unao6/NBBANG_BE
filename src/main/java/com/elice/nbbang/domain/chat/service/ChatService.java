package com.elice.nbbang.domain.chat.service;

import com.elice.nbbang.domain.chat.controller.WebSocketController;
import com.elice.nbbang.domain.chat.dto.ArchivedChatDto;
import com.elice.nbbang.domain.chat.dto.ArchivedChatListDto;
import com.elice.nbbang.domain.chat.dto.Message;
import com.elice.nbbang.domain.chat.entity.ArchivedChats;
import com.elice.nbbang.domain.chat.entity.Chat;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.chat.repository.ArchivedChatRepository;
import com.elice.nbbang.domain.chat.repository.ChatRepository;
import com.elice.nbbang.domain.user.repository.UserRepository;
import com.elice.nbbang.domain.chat.exception.UserNotFoundException;
import com.elice.nbbang.domain.chat.exception.ChatNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final ArchivedChatRepository archivedChatRepository;
    private final HashOperations<String, String, Integer> hashOps;

    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);


    public ChatService(UserRepository userRepository,
                       ChatRepository chatRepository,
                       ArchivedChatRepository archivedChatRepository,
                       @Qualifier("redisTemplateForChat") RedisTemplate<String, Object> redisTemplate) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.archivedChatRepository = archivedChatRepository;
        this.hashOps = redisTemplate.opsForHash();
    }

    // 이전에 진행중이던 상담이 있으면 가져와서 이어가기, 없다면 새로운 상담 생성
    @Transactional
    public Long getOrCreateChat(Long userId) {


        Optional<Chat> optionalChat = chatRepository.findByUserIdAndStatus(userId, true);


        System.out.println(optionalChat.isPresent());
        if (optionalChat.isPresent()) {
            return optionalChat.get().getId();
        } else {
            // Create a new chat
            Chat newChat = new Chat();
            User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
            newChat.setUser(user);
            newChat.setStatus(true);
            return chatRepository.save(newChat).getId();

        }
    }


    // 메시지 전송
    @Transactional
    public Chat sendMessage(Long chatId, Message message) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(ChatNotFoundException::new);

        if(message.getNickname().equals("System")) {
            return chat;
        }

        chat.getMessages().add(message);
        chat.setLastRepliedAt(LocalDateTime.now());

        hashOps.increment("chat:newMessages", chatId.toString(), 1);

        return chatRepository.save(chat);
    }

    public Map<Long, Integer> getAllNewMessagesCount() {
        return hashOps.entries("chat:newMessages")
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> Long.parseLong(entry.getKey()),
                        entry -> {
                            Object value = entry.getValue();
                            if (value instanceof Integer) {
                                return (Integer) value;
                            } else if (value instanceof String) {
                                return Integer.parseInt((String) value);
                            } else {
                                throw new IllegalStateException("Unexpected value type: " + value.getClass());
                            }
                        }
                ));
    }

    public void resetNewMessagesCount(Long chatId) {
        hashOps.put("chat:newMessages", chatId.toString(), 0);
    }

    // 상담 종료
    @Transactional
    public void endChat(Long chatId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(ChatNotFoundException::new);
        chat.setStatus(false);
        chat.setEndedAt(LocalDateTime.now());
        chatRepository.save(chat);
    }

    // 모든 상담 조회
    public List<Chat> getAllChats() {
        return chatRepository.findAll();
    }

    // 특정 채팅방 메시지 조회
    public List<Message> getChatMessages(Long chatId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(ChatNotFoundException::new);
        return chat.getMessages();
    }

    // 상담 영구 저장
    @Transactional
    public void archiveChat(Long chatId, String memo) {
        // 이미 아카이브된 대화인지 확인
        boolean alreadyArchived = archivedChatRepository.existsByChatId(chatId);
        if (alreadyArchived) {
            throw new IllegalStateException("이미 저장된 채팅입니다.");
        }

        Chat chat = chatRepository.findById(chatId).orElseThrow(ChatNotFoundException::new);
        ArchivedChats archivedChat = new ArchivedChats();
        archivedChat.setUserId(chat.getUser().getId());
        archivedChat.setChatId(chatId);
        archivedChat.setMessages(new ArrayList<>(chat.getMessages()));
        archivedChat.setEndedAt(chat.getEndedAt());
        archivedChat.setSavedAt(LocalDateTime.now());
        archivedChat.setMemo(memo);
        archivedChatRepository.save(archivedChat);
    }

    // 아카이브된 상담 조회
    public List<ArchivedChatListDto> getArchivedChats() {
        return archivedChatRepository.findAll().stream()
                .map(chat -> new ArchivedChatListDto(
                        chat.getId(),
                        chat.getMemo(),
                        chat.getMessages(),
                        chat.getEndedAt(),
                        chat.getSavedAt()))
                .collect(Collectors.toList());

    }

    // 특정 아카이브된 상담 조회
    public ArchivedChatDto getArchivedChat(Long archivedId) {
        ArchivedChats archivedChats = archivedChatRepository.findById(archivedId).orElseThrow(ChatNotFoundException::new);
        return new ArchivedChatDto(
                archivedChats.getId(),
                archivedChats.getMessages()
        );
    }

    // 내용이 없는 채팅 삭제
    @Transactional
    public void deleteEmptyChat() {
        List<Chat> emptyChats = chatRepository.findEmptyChats();
        chatRepository.deleteAll(emptyChats);
    }


    // 마지막 응답 이후 24시간이 지난 상담 종료 처리
    @Scheduled(cron = "0 0 * * * ?") // 매시간 정각에 실행
    @Transactional
    public void endChatsAfter24Hours() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusHours(24);
        List<Chat> chatsToClose = chatRepository.findByLastRepliedAtAndStatus(cutoffDate, true);
        for (Chat chat : chatsToClose) {
            chat.setStatus(false);
            chat.setEndedAt(LocalDateTime.now());
            chatRepository.save(chat);
        }
    }

    // 상담 종료 후 30일이 지난 상담 삭제
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    @Transactional
    public void deleteOldChats() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        chatRepository.deleteByEndedAtAndStatus(cutoffDate, false);
    }

}
