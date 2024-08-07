package com.elice.nbbang.domain.chat.controller;

import com.elice.nbbang.domain.chat.dto.ChatListResponse;
import com.elice.nbbang.domain.chat.dto.Message;
import com.elice.nbbang.domain.chat.entity.ArchivedChats;
import com.elice.nbbang.domain.chat.entity.Chat;
import com.elice.nbbang.domain.chat.service.ChatService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/chat")
public class AdminChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public AdminChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    // 모든 상담 목록 조회
    @GetMapping("/all")
    public ResponseEntity<List<ChatListResponse>> getAllChats() {
        List<Chat> chats = chatService.getAllChats();
        List<ChatListResponse> chatListResponses = chats.stream()
                .map(ChatListResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(chatListResponses);
    }

    // 특정 채팅방 메세지 조회
    @GetMapping("/{chatId}")
    public ResponseEntity<List<Message>> getChatMessages(@PathVariable("chatId") Long chatId) {
        List<Message> messages = chatService.getChatMessages(chatId);
        return ResponseEntity.ok(messages);
    }

    // 상담 종료
    @PostMapping("/end/{chatId}")
    public ResponseEntity<String> endChat(@PathVariable Long chatId) {
        chatService.endChat(chatId);
        return ResponseEntity.ok("Chat ended successfully");
    }

    // 상담 영구 저장 (아카이브)
    @PostMapping("/archive/{chatId}")
    public ResponseEntity<String> archiveChat(@PathVariable Long chatId, @RequestBody String memo) {
        try {
            chatService.archiveChat(chatId, memo);
            return ResponseEntity.ok("채팅이 성공적으로 저장되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 저장된 채팅입니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("알 수 없는 에러 발생.");
        }
    }

    // 아카이브된 채팅 목록 조회
    @GetMapping("/archived")
    public ResponseEntity<List<ArchivedChats>> getArchivedChats() {
        List<ArchivedChats> archivedChats = chatService.getArchivedChats();
        return ResponseEntity.ok(archivedChats);
    }

    // 특정 사용자의 아카이브된 채팅 조회
    @GetMapping("/archived/{archivedId}")
    public ResponseEntity<ArchivedChats> getArchivedChat(@PathVariable Long archivedId) {
        ArchivedChats archivedChat = chatService.getArchivedChat(archivedId);
        return ResponseEntity.ok(archivedChat);
    }

}
