package br.ifsp.ctrltcc.dto.chat;

import java.time.LocalDateTime;

public class ChatDTO {

    public record ChatResponse(
            Long id,
            Long projectId,
            String projectTitle,
            LocalDateTime createdAt
    ) {
    }

    private ChatDTO() {
    }
}
