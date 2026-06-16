package br.ifsp.ctrltcc.service;

import br.ifsp.ctrltcc.dto.chat.ChatMessageDTOs.ChatMessageResponse;
import br.ifsp.ctrltcc.dto.chat.ChatMessageDTOs.SendMessageRequest;
import br.ifsp.ctrltcc.mapper.ChatMapper;
import br.ifsp.ctrltcc.model.ChatMessage;
import br.ifsp.ctrltcc.model.ChatRoom;
import br.ifsp.ctrltcc.model.User;
import br.ifsp.ctrltcc.repository.ChatMessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ChatMessageService {

    private final ChatMessageRepository messageRepository;
    private final ChatRoomService chatRoomService;
    private final ChatMapper chatMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatMessageService(ChatMessageRepository messageRepository,
                              ChatRoomService chatRoomService,
                              ChatMapper chatMapper,
                              SimpMessagingTemplate messagingTemplate) {
        this.messageRepository = messageRepository;
        this.chatRoomService = chatRoomService;
        this.chatMapper = chatMapper;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Persiste a mensagem e faz broadcast para todos os membros da sala
     * via tópico STOMP: /topic/rooms/{roomId}
     */
    public ChatMessageResponse send(Long roomId, SendMessageRequest req, User sender) {
        ChatRoom room = chatRoomService.getRoomOrThrow(roomId);
        chatRoomService.assertMember(room, sender);

        ChatMessage message = messageRepository.save(new ChatMessage(req.content(), sender, room));
        ChatMessageResponse response = chatMapper.toMessageResponse(message);

        messagingTemplate.convertAndSend("/topic/rooms/" + roomId, response);

        return response;
    }

    /**
     * Histórico paginado — mais recentes primeiro.
     * Uso típico: GET /api/rooms/{id}/messages?page=0&size=30
     */
    @Transactional(readOnly = true)
    public Page<ChatMessageResponse> getHistory(Long roomId, User requester, Pageable pageable) {
        ChatRoom room = chatRoomService.getRoomOrThrow(roomId);
        chatRoomService.assertMember(room, requester);

        return messageRepository.findByRoomOrderBySentAtDesc(room, pageable)
                .map(chatMapper::toMessageResponse);
    }
}
