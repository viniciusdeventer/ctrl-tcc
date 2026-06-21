package br.ifsp.ctrltcc.controller;

import br.ifsp.ctrltcc.dto.chat.ChatDTO.ChatResponse;
import br.ifsp.ctrltcc.dto.chat.MessageDTO.MessageResponse;
import br.ifsp.ctrltcc.dto.chat.MessageDTO.SendMessageRequest;
import br.ifsp.ctrltcc.model.User;
import br.ifsp.ctrltcc.security.AuthenticatedUserResolver;
import br.ifsp.ctrltcc.service.ChatService;
import br.ifsp.ctrltcc.service.MessageService;
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

/**
 * Chat e 1:1 com Project e nasce automaticamente quando a proposta e aceita
 * (ver ProjectService#acceptProposalAndCreateProject). Por isso este controller
 * nao tem endpoints de criacao de chat nem de gestao de membros -- "ser membro
 * do chat" e exatamente "ser ProjectMember do projeto".
 */
@RestController
public class ChatController {

    private final ChatService chatService;
    private final MessageService messageService;
    private final AuthenticatedUserResolver userResolver;

    public ChatController(
            ChatService chatService,
            MessageService messageService,
            AuthenticatedUserResolver userResolver
    ) {
        this.chatService = chatService;
        this.messageService = messageService;
        this.userResolver = userResolver;
    }

    @GetMapping("/api/chats/{chatId}")
    public ResponseEntity<ChatResponse> getChat(@PathVariable Long chatId) {
        return ResponseEntity.ok(chatService.findById(chatId, userResolver.current()));
    }

    @GetMapping("/api/projects/{projectId}/chat")
    public ResponseEntity<ChatResponse> getChatByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(chatService.findByProject(projectId, userResolver.current()));
    }

    @GetMapping("/api/chats/{chatId}/messages")
    public ResponseEntity<Page<MessageResponse>> history(
            @PathVariable Long chatId,
            @PageableDefault(size = 30) Pageable pageable
    ) {
        return ResponseEntity.ok(
                messageService.getHistory(chatId, userResolver.current(), pageable)
        );
    }

    @MessageMapping("/chats/{chatId}/send")
    public void sendMessage(
            @DestinationVariable Long chatId,
            @Payload @Valid SendMessageRequest request,
            Authentication authentication
    ) {
        User sender = userResolver.resolve(authentication);
        messageService.send(chatId, request, sender);
    }
}
