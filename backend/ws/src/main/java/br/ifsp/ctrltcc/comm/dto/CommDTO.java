package br.ifsp.ctrltcc.comm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class CommDTO {

    public record CreateChatRequest(
            @NotNull(message = "projectId is required")
            Long projectId
    ) {
    }

    public record ChatResponse(
            Long id,
            Long projectId,
            LocalDateTime createdAt
    ) {
    }

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

    private CommDTO() {
    }
}
