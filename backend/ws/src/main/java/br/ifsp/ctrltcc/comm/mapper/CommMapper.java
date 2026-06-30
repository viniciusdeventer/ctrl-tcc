package br.ifsp.ctrltcc.comm.mapper;

import br.ifsp.ctrltcc.comm.dto.CommDTO.ChatResponse;
import br.ifsp.ctrltcc.comm.dto.CommDTO.MessageResponse;
import br.ifsp.ctrltcc.comm.model.Chat;
import br.ifsp.ctrltcc.comm.model.Message;
import org.springframework.stereotype.Component;

@Component
public class CommMapper {

    public ChatResponse toChatResponse(Chat chat) {
        return new ChatResponse(
                chat.getId(),
                chat.getProjectId(),
                chat.getCreatedAt()
        );
    }

    public MessageResponse toMessageResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getChat().getId(),
                message.getSenderId(),
                message.getSenderName(),
                message.getContent(),
                message.getSentAt()
        );
    }
}
