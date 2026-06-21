package br.ifsp.ctrltcc.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "project_member",
        uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "user_id"})
)
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectRole role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    private void prePersist() {
        this.joinedAt = LocalDateTime.now();
    }

    protected ProjectMember() {
    }

    public ProjectMember(Project project, User user, ProjectRole role) {
        this.project = project;
        this.user = user;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public User getUser() {
        return user;
    }

    public ProjectRole getRole() {
        return role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
}
