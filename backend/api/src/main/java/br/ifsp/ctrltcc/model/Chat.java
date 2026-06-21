package br.ifsp.ctrltcc.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(nullable = false, unique = true)
    private Project project;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
    }

    protected Chat() {
    }

    public Chat(Project project) {
        this.project = project;
    }

    public Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
