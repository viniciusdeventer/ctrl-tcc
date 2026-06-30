package br.ifsp.ctrltcc.comm.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime sentAt;

    // Referencia o User do monolito apenas por ID.
    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    // Desnormalizado para evitar chamada ao monolito a cada mensagem exibida.
    @Column(name = "sender_name", nullable = false)
    private String senderName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @PrePersist
    private void prePersist() {
        this.sentAt = LocalDateTime.now();
    }

    protected Message() {
    }

    public Message(String content, Long senderId, String senderName, Chat chat) {
        this.content = content;
        this.senderId = senderId;
        this.senderName = senderName;
        this.chat = chat;
    }

    public Long getId() { return id; }
    public String getContent() { return content; }
    public LocalDateTime getSentAt() { return sentAt; }
    public Long getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public Chat getChat() { return chat; }
}
