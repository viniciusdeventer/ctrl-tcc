package br.ifsp.ctrltcc.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @PrePersist
    private void prePersist() {
        this.sentAt = LocalDateTime.now();
    }

    protected Message() {
    }

    public Message(String content, User sender, Chat chat) {
        this.content = content;
        this.sender = sender;
        this.chat = chat;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public User getSender() {
        return sender;
    }

    public Chat getChat() {
        return chat;
    }
}
