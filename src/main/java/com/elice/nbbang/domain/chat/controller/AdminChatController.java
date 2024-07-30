package com.elice.nbbang.domain.chat.controller;

import com.elice.nbbang.domain.chat.dto.Message;
import com.elice.nbbang.domain.chat.entity.ArchivedChats;
import com.elice.nbbang.domain.chat.entity.Chat;
import com.elice.nbbang.domain.chat.service.ChatService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/admin/chat")
public class AdminChatController {
    private final ChatService chatService;

    public AdminChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // 모든 상담 목록 조회
    @GetMapping("/all")
    public ResponseEntity<List<Chat>> getAllChats() {
        List<Chat> chats = chatService.getAllChats();
        return ResponseEntity.ok(chats);
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
    @PostMapping("/archive")
    public ResponseEntity<String> archiveChat(@RequestBody Long chatId, @RequestParam String memo) {
        chatService.archiveChat(chatId, memo);
        return ResponseEntity.ok("Chat archived successfully");
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
