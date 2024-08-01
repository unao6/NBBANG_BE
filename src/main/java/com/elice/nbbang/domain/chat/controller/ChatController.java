package com.elice.nbbang.domain.chat.controller;

import com.elice.nbbang.domain.chat.dto.ChatResponse;
import com.elice.nbbang.domain.chat.dto.Message;
import com.elice.nbbang.domain.chat.service.ChatService;
import com.elice.nbbang.domain.user.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController{

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // '상담 시작' 버튼으로 채팅을 시작 -> 웹소켓 연결 및 chatId, 기존메세지 반환
    @PostMapping("/start")
    public ResponseEntity<ChatResponse> startChat(@AuthenticationPrincipal User user) {
        Long chatId = chatService.startChat(user.getId());
        List<Message> messages = chatId != -1 ? chatService.getChatMessages(chatId) : new ArrayList<>();
        return ResponseEntity.ok(new ChatResponse(chatId, messages));
    }

//    @PostMapping("/start")
//    public ResponseEntity<ChatResponse> startChat(@RequestBody User user) {
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
//        }
//        Long chatId = chatService.startChat(user.getId());
//        List<Message> messages = chatId != -1 ? chatService.getChatMessages(chatId) : new ArrayList<>();
//        return ResponseEntity.ok(new ChatResponse(chatId, messages));
//    }
}
