package br.ifsp.ctrltcc.comm.controller;

import br.ifsp.ctrltcc.comm.dto.CommDTO.ChatResponse;
import br.ifsp.ctrltcc.comm.dto.CommDTO.CreateChatRequest;
import br.ifsp.ctrltcc.comm.dto.CommDTO.MessageResponse;
import br.ifsp.ctrltcc.comm.dto.CommDTO.SendMessageRequest;
import br.ifsp.ctrltcc.comm.security.CallerContext;
import br.ifsp.ctrltcc.comm.security.CallerResolver;
import br.ifsp.ctrltcc.comm.service.ChatService;
import br.ifsp.ctrltcc.comm.service.MessageService;
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

import java.net.URI;

@RestController
public class ChatController {

    private final ChatService chatService;
    private final MessageService messageService;
    private final CallerResolver callerResolver;

    public ChatController(
            ChatService chatService,
            MessageService messageService,
            CallerResolver callerResolver
    ) {
        this.chatService = chatService;
        this.messageService = messageService;
        this.callerResolver = callerResolver;
    }

    /**
     * Endpoint interno chamado pelo monolito ao criar um Project.
     * Nao requer autenticacao de usuario final — o monolito chama com
     * seu proprio token de servico (ou API key em producao).
     */
    @PostMapping("/internal/chats")
    public ResponseEntity<ChatResponse> createForProject(@Valid @RequestBody CreateChatRequest request) {
        ChatResponse response = chatService.createForProject(request.projectId());
        return ResponseEntity
                .created(URI.create("/api/chats/" + response.id()))
                .body(response);
    }

    @GetMapping("/api/projects/{projectId}/chat")
    public ResponseEntity<ChatResponse> getByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(chatService.findByProject(projectId, callerResolver.current()));
    }

    @GetMapping("/api/chats/{chatId}")
    public ResponseEntity<ChatResponse> getById(@PathVariable Long chatId) {
        return ResponseEntity.ok(chatService.findById(chatId, callerResolver.current()));
    }

    @GetMapping("/api/chats/{chatId}/messages")
    public ResponseEntity<Page<MessageResponse>> history(
            @PathVariable Long chatId,
            @PageableDefault(size = 30) Pageable pageable
    ) {
        return ResponseEntity.ok(
                messageService.getHistory(chatId, callerResolver.current(), pageable));
    }

    @PostMapping("/api/chats/{chatId}/messages")
    public ResponseEntity<MessageResponse> sendViaHttp(
            @PathVariable Long chatId,
            @Valid @RequestBody SendMessageRequest request
    ) {
        return ResponseEntity.ok(messageService.send(chatId, request, callerResolver.current()));
    }

    @MessageMapping("/chats/{chatId}/send")
    public void sendViaWebSocket(
            @DestinationVariable Long chatId,
            @Payload @Valid SendMessageRequest request,
            Authentication authentication
    ) {
        CallerContext caller = callerResolver.resolve(authentication);
        messageService.send(chatId, request, caller);
    }
    
    
}
