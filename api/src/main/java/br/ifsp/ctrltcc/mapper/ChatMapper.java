package br.ifsp.ctrltcc.mapper;

import br.ifsp.ctrltcc.dto.chat.ChatMessageDTOs.ChatMessageResponse;
import br.ifsp.ctrltcc.dto.chat.ChatRoomDTOs.ChatRoomResponse;
import br.ifsp.ctrltcc.dto.chat.ChatRoomDTOs.RoomMemberResponse;
import br.ifsp.ctrltcc.model.ChatMessage;
import br.ifsp.ctrltcc.model.ChatRoom;
import br.ifsp.ctrltcc.model.User;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class ChatMapper {

    public RoomMemberResponse toMemberResponse(User user) {
        return new RoomMemberResponse(user.getId(), user.getName(), user.getEmail());
    }

    public ChatRoomResponse toRoomResponse(ChatRoom room) {
        List<RoomMemberResponse> members = room.getMembers().stream()
                .map(this::toMemberResponse)
                .sorted(Comparator.comparing(RoomMemberResponse::name))
                .toList();

        return new ChatRoomResponse(
                room.getId(),
                room.getName(),
                room.getDescription(),
                room.getCreatedAt(),
                toMemberResponse(room.getCreatedBy()),
                members
        );
    }

    public ChatMessageResponse toMessageResponse(ChatMessage message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getContent(),
                message.getSentAt(),
                message.getSender().getId(),
                message.getSender().getName(),
                message.getRoom().getId()
        );
    }
}
