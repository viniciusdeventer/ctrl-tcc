package br.ifsp.ctrltcc.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class ChatRoomDTOs {

    public record CreateRoomRequest(
            @NotBlank(message = "Nome da sala é obrigatório")
            String name,

            String description,

            @NotEmpty(message = "A sala deve ter ao menos um outro membro")
            Set<Long> memberIds
    ) {}

    public record AddMemberRequest(
            @NotEmpty(message = "Informe ao menos um usuário")
            Set<Long> userIds
    ) {}

    public record RoomMemberResponse(
            Long id,
            String name,
            String email
    ) {}

    public record ChatRoomResponse(
            Long id,
            String name,
            String description,
            LocalDateTime createdAt,
            RoomMemberResponse createdBy,
            List<RoomMemberResponse> members
    ) {}
}
