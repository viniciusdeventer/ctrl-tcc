package br.ifsp.ctrltcc.dto.chat;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class MessageDTO {


    public record SendMessageRequest(
            @NotBlank(message = "Conteúdo da mensagem é obrigatório")
            String content
    ) {}


    public record MessageResponse(
            Long id,
            String content,
            LocalDateTime sentAt,
            Long senderId,
            String senderName,
            Long chatId
    ) {}
}
