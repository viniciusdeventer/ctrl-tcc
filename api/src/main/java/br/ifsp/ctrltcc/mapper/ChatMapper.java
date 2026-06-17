package br.ifsp.ctrltcc.mapper;

import br.ifsp.ctrltcc.dto.chat.MessageDTO.MessageResponse;
import br.ifsp.ctrltcc.dto.chat.ChatDTO.ChatResponse;
import br.ifsp.ctrltcc.dto.chat.ChatDTO.ChatMemberResponse;
import br.ifsp.ctrltcc.model.Message;
import br.ifsp.ctrltcc.model.Chat;
import br.ifsp.ctrltcc.model.User;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class ChatMapper {

    public ChatMemberResponse toMemberResponse(User user) {
        return new ChatMemberResponse(user.getId(), user.getName(), user.getEmail());
    }

    public ChatResponse toChatResponse(Chat chat) {
        List<ChatMemberResponse> members = chat.getMembers().stream()
                .map(this::toMemberResponse)
                .sorted(Comparator.comparing(ChatMemberResponse::name))
                .toList();

        return new ChatResponse(
                chat.getId(),
                chat.getName(),
                chat.getDescription(),
                chat.getCreatedAt(),
                toMemberResponse(chat.getCreatedBy()),
                members
        );
    }

    public MessageResponse toMessageResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getContent(),
                message.getSentAt(),
                message.getSender().getId(),
                message.getSender().getName(),
                message.getChat().getId()
        );
    }
}
