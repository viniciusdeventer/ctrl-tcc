package br.ifsp.ctrltcc.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class MessageDTO {

    public record SendMessageRequest(
            @NotBlank(message = "Content is required")
            @Size(max = 5000, message = "Content must be at most 5000 characters")
            String content
    ) {
    }

    public record MessageResponse(
            Long id,
            Long chatId,
            Long senderId,
            String senderName,
            String content,
            LocalDateTime sentAt
    ) {
    }

    private MessageDTO() {
    }
}
