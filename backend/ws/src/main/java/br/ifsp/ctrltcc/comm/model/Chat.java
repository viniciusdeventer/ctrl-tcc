package br.ifsp.ctrltcc.comm.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Chat agora existe no dominio de Communication.
 * Referencia o Project do monolito apenas por ID.
 * A integridade e garantida pelo fluxo: o monolito so cria o chat
 * apos persistir o Project com sucesso.
 */
@Entity
@Table(name = "chat", uniqueConstraints = @UniqueConstraint(columnNames = "project_id"))
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false, unique = true)
    private Long projectId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    protected Chat() {
    }

    public Chat(Long projectId) {
        this.projectId = projectId;
    }

    public Long getId() {
        return id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
