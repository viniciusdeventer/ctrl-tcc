package br.ifsp.ctrltcc.service;

import br.ifsp.ctrltcc.dto.chat.MessageDTO.MessageResponse;
import br.ifsp.ctrltcc.dto.chat.MessageDTO.SendMessageRequest;
import br.ifsp.ctrltcc.mapper.ChatMapper;
import br.ifsp.ctrltcc.model.Chat;
import br.ifsp.ctrltcc.model.Message;
import br.ifsp.ctrltcc.model.User;
import br.ifsp.ctrltcc.repository.MessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatService chatService;
    private final ChatMapper chatMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageService(
            MessageRepository messageRepository,
            ChatService chatService,
            ChatMapper chatMapper,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.messageRepository = messageRepository;
        this.chatService = chatService;
        this.chatMapper = chatMapper;
        this.messagingTemplate = messagingTemplate;
    }

    public MessageResponse send(Long chatId, SendMessageRequest request, User sender) {
        Chat chat = chatService.getChatOrThrow(chatId);
        chatService.assertMember(chat, sender);

        Message message = messageRepository.save(new Message(request.content(), sender, chat));
        MessageResponse response = chatMapper.toMessageResponse(message);

        messagingTemplate.convertAndSend("/topic/chats/" + chatId, response);
        return response;
    }

    @Transactional(readOnly = true)
    public Page<MessageResponse> getHistory(Long chatId, User requester, Pageable pageable) {
        Chat chat = chatService.getChatOrThrow(chatId);
        chatService.assertMember(chat, requester);

        return messageRepository.findByChatOrderBySentAtDesc(chat, pageable)
                .map(chatMapper::toMessageResponse);
    }
}
