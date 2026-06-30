package br.ifsp.ctrltcc.comm.service;

import br.ifsp.ctrltcc.comm.dto.CommDTO.MessageResponse;
import br.ifsp.ctrltcc.comm.dto.CommDTO.SendMessageRequest;
import br.ifsp.ctrltcc.comm.mapper.CommMapper;
import br.ifsp.ctrltcc.comm.model.Chat;
import br.ifsp.ctrltcc.comm.model.Message;
import br.ifsp.ctrltcc.comm.repository.MessageRepository;
import br.ifsp.ctrltcc.comm.security.CallerContext;
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
    private final CommMapper commMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageService(
            MessageRepository messageRepository,
            ChatService chatService,
            CommMapper commMapper,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.messageRepository = messageRepository;
        this.chatService = chatService;
        this.commMapper = commMapper;
        this.messagingTemplate = messagingTemplate;
    }

    public MessageResponse send(Long chatId, SendMessageRequest request, CallerContext caller) {
        Chat chat = chatService.getChatOrThrow(chatId);
        chatService.assertMember(chat.getProjectId(), caller);

        Message message = messageRepository.save(
                new Message(request.content(), caller.userId(), caller.name(), chat)
        );

        MessageResponse response = commMapper.toMessageResponse(message);
        messagingTemplate.convertAndSend("/topic/chats/" + chatId, response);
        return response;
    }

    @Transactional(readOnly = true)
    public Page<MessageResponse> getHistory(Long chatId, CallerContext caller, Pageable pageable) {
        Chat chat = chatService.getChatOrThrow(chatId);
        chatService.assertMember(chat.getProjectId(), caller);
        return messageRepository.findByChatOrderBySentAtDesc(chat, pageable)
                .map(commMapper::toMessageResponse);
    }
}
