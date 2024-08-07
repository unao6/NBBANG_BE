package com.elice.nbbang.domain.chat.controller;

import com.elice.nbbang.domain.chat.dto.ChatResponse;
import com.elice.nbbang.domain.chat.dto.Message;
import com.elice.nbbang.domain.chat.service.ChatService;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.service.UserService;
import com.elice.nbbang.global.util.UserUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@AllArgsConstructor
public class ChatController{

    private final ChatService chatService;
    private final UserUtil userUtil;
    private final UserService userService;


    // '상담 시작' 버튼으로 채팅을 시작 -> 웹소켓 연결 및 chatId, 기존메세지 반환
    @PostMapping("/start")
    public ResponseEntity<ChatResponse> startChat() {
        String userEmail = userUtil.getAuthenticatedUserEmail();
        if (userEmail == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 인증되지 않은 경우
        }

        // 이메일을 통해 사용자 조회
        User user = userService.findByEmail(userEmail);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 사용자가 존재하지 않는 경우
        }

        Long chatId = chatService.startChat(user.getId());
        List<Message> messages = chatId != -1 ? chatService.getChatMessages(chatId) : new ArrayList<>();
        return ResponseEntity.ok(new ChatResponse(chatId, messages));
    }
}
