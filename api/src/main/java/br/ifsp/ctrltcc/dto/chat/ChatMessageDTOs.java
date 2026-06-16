package br.ifsp.ctrltcc.dto.chat;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class ChatMessageDTOs {

    // ── Request (enviado via STOMP) ───────────────────────────────────────────

    public record SendMessageRequest(
            @NotBlank(message = "Conteúdo da mensagem é obrigatório")
            String content
    ) {}

    // ── Response (broadcast STOMP + histórico REST) ───────────────────────────

    public record ChatMessageResponse(
            Long id,
            String content,
            LocalDateTime sentAt,
            Long senderId,
            String senderName,
            Long roomId
    ) {}
}
