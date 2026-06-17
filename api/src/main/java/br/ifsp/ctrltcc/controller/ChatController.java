package br.ifsp.ctrltcc.controller;

import br.ifsp.ctrltcc.dto.chat.MessageDTO.MessageResponse;
import br.ifsp.ctrltcc.dto.chat.MessageDTO.SendMessageRequest;
import br.ifsp.ctrltcc.dto.chat.ChatDTO.AddMemberRequest;
import br.ifsp.ctrltcc.dto.chat.ChatDTO.ChatResponse;
import br.ifsp.ctrltcc.dto.chat.ChatDTO.CreateChatRequest;
import br.ifsp.ctrltcc.model.User;
import br.ifsp.ctrltcc.security.AuthenticatedUserResolver;
import br.ifsp.ctrltcc.service.MessageService;
import br.ifsp.ctrltcc.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
public class ChatController {

    private final ChatService chatService;
    private final MessageService messageService;
    private final AuthenticatedUserResolver userResolver;

    public ChatController(ChatService chatService,
                          MessageService messageService,
                          AuthenticatedUserResolver userResolver) {
        this.chatService = chatService;
        this.messageService = messageService;
        this.userResolver = userResolver;
    }

    @PostMapping("/api/chats")
    public ResponseEntity<ChatResponse> createChat(@Valid @RequestBody CreateChatRequest request) {
        User creator = userResolver.current();
        ChatResponse chat = chatService.create(request, creator);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(chat.id()).toUri();
        return ResponseEntity.created(location).body(chat);
    }

    @GetMapping("/api/chats")
    public ResponseEntity<List<ChatResponse>> myChats() {
        return ResponseEntity.ok(chatService.findMyChats(userResolver.current()));
    }

    @GetMapping("/api/chats/{chatId}")
    public ResponseEntity<ChatResponse> getChat(@PathVariable Long chatId) {
        return ResponseEntity.ok(chatService.findById(chatId, userResolver.current()));
    }

    @PostMapping("/api/chats/{chatId}/members")
    public ResponseEntity<ChatResponse> addMembers(
            @PathVariable Long chatId,
            @Valid @RequestBody AddMemberRequest request) {
        return ResponseEntity.ok(chatService.addMembers(chatId, request, userResolver.current()));
    }

    @DeleteMapping("/api/chats/{chatId}/members/{userId}")
    public ResponseEntity<ChatResponse> removeMember(
            @PathVariable Long chatId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(chatService.removeMember(chatId, userId, userResolver.current()));
    }

    @GetMapping("/api/chats/{chatId}/messages")
    public ResponseEntity<Page<MessageResponse>> history(
            @PathVariable Long chatId,
            @PageableDefault(size = 30) Pageable pageable) {
        return ResponseEntity.ok(
                messageService.getHistory(chatId, userResolver.current(), pageable));
    }

    @MessageMapping("/chats/{chatId}/send")
    public void sendMessage(
            @DestinationVariable Long chatId,
            @Payload @Valid SendMessageRequest request,
            Authentication authentication) {

        User sender = userResolver.resolve(authentication);
        messageService.send(chatId, request, sender);
    }
}
