package br.ifsp.ctrltcc.mapper;

import br.ifsp.ctrltcc.dto.chat.ChatDTO.ChatResponse;
import br.ifsp.ctrltcc.dto.chat.MessageDTO.MessageResponse;
import br.ifsp.ctrltcc.model.Chat;
import br.ifsp.ctrltcc.model.Message;
import org.springframework.stereotype.Component;

@Component
public class ChatMapper {

    public ChatResponse toChatResponse(Chat chat) {
        return new ChatResponse(
                chat.getId(),
                chat.getProject().getId(),
                chat.getProject().getTitle(),
                chat.getCreatedAt()
        );
    }

    public MessageResponse toMessageResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getChat().getId(),
                message.getSender().getId(),
                message.getSender().getName(),
                message.getContent(),
                message.getSentAt()
        );
    }
}
