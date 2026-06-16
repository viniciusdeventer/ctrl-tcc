package br.ifsp.ctrltcc.controller;

import br.ifsp.ctrltcc.dto.chat.ChatMessageDTOs.ChatMessageResponse;
import br.ifsp.ctrltcc.dto.chat.ChatMessageDTOs.SendMessageRequest;
import br.ifsp.ctrltcc.dto.chat.ChatRoomDTOs.AddMemberRequest;
import br.ifsp.ctrltcc.dto.chat.ChatRoomDTOs.ChatRoomResponse;
import br.ifsp.ctrltcc.dto.chat.ChatRoomDTOs.CreateRoomRequest;
import br.ifsp.ctrltcc.model.User;
import br.ifsp.ctrltcc.security.AuthenticatedUserResolver;
import br.ifsp.ctrltcc.service.ChatMessageService;
import br.ifsp.ctrltcc.service.ChatRoomService;
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

    private final ChatRoomService roomService;
    private final ChatMessageService messageService;
    private final AuthenticatedUserResolver userResolver;

    public ChatController(ChatRoomService roomService,
                          ChatMessageService messageService,
                          AuthenticatedUserResolver userResolver) {
        this.roomService = roomService;
        this.messageService = messageService;
        this.userResolver = userResolver;
    }

    // ── REST: Salas ───────────────────────────────────────────────────────────

    /** Cria uma nova sala e adiciona os membros informados. */
    @PostMapping("/api/rooms")
    public ResponseEntity<ChatRoomResponse> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        User creator = userResolver.current();
        ChatRoomResponse room = roomService.create(request, creator);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(room.id()).toUri();
        return ResponseEntity.created(location).body(room);
    }

    /** Lista todas as salas do usuário autenticado. */
    @GetMapping("/api/rooms")
    public ResponseEntity<List<ChatRoomResponse>> myRooms() {
        return ResponseEntity.ok(roomService.findMyRooms(userResolver.current()));
    }

    /** Detalhes de uma sala (apenas membros). */
    @GetMapping("/api/rooms/{roomId}")
    public ResponseEntity<ChatRoomResponse> getRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.findById(roomId, userResolver.current()));
    }

    /** Adiciona membros a uma sala existente. */
    @PostMapping("/api/rooms/{roomId}/members")
    public ResponseEntity<ChatRoomResponse> addMembers(
            @PathVariable Long roomId,
            @Valid @RequestBody AddMemberRequest request) {
        return ResponseEntity.ok(roomService.addMembers(roomId, request, userResolver.current()));
    }

    /** Remove um membro da sala (o criador não pode ser removido). */
    @DeleteMapping("/api/rooms/{roomId}/members/{userId}")
    public ResponseEntity<ChatRoomResponse> removeMember(
            @PathVariable Long roomId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(roomService.removeMember(roomId, userId, userResolver.current()));
    }

    // ── REST: Histórico de mensagens ──────────────────────────────────────────

    /**
     * Histórico paginado da sala — mais recentes primeiro.
     * Exemplo: GET /api/rooms/1/messages?page=0&size=30
     */
    @GetMapping("/api/rooms/{roomId}/messages")
    public ResponseEntity<Page<ChatMessageResponse>> history(
            @PathVariable Long roomId,
            @PageableDefault(size = 30) Pageable pageable) {
        return ResponseEntity.ok(
                messageService.getHistory(roomId, userResolver.current(), pageable));
    }

    // ── STOMP: Envio de mensagem em tempo real ────────────────────────────────

    /**
     * Cliente envia para: /app/rooms/{roomId}/send
     * Servidor faz broadcast para: /topic/rooms/{roomId}
     *
     * Header STOMP na conexão: Authorization: Bearer <token>
     */
    @MessageMapping("/rooms/{roomId}/send")
    public void sendMessage(
            @DestinationVariable Long roomId,
            @Payload @Valid SendMessageRequest request,
            Authentication authentication) {

        User sender = userResolver.resolve(authentication);
        messageService.send(roomId, request, sender);
    }
}
