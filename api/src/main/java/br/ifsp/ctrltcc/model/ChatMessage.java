package br.ifsp.ctrltcc.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

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
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom room;

    @PrePersist
    private void prePersist() {
        this.sentAt = LocalDateTime.now();
    }

    public ChatMessage() {}

    public ChatMessage(String content, User sender, ChatRoom room) {
        this.content = content;
        this.sender = sender;
        this.room = room;
    }

    public Long getId() { return id; }

    public String getContent() { return content; }

    public LocalDateTime getSentAt() { return sentAt; }

    public User getSender() { return sender; }

    public ChatRoom getRoom() { return room; }
}
