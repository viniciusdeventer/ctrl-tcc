package br.ifsp.ctrltcc.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 5000)
    private String description;

    @OneToOne(optional = false)
    @JoinColumn(nullable = false, unique = true)
    private Proposal proposal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectMember> members = new ArrayList<>();

    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Chat chat;

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
        status = ProjectStatus.ACTIVE;
    }

    protected Project() {
    }

    public Project(Proposal proposal) {
        if (!proposal.isAccepted()) {
            throw new IllegalArgumentException(
                    "Only accepted proposals can become projects."
            );
        }
        this.proposal = proposal;
        this.title = proposal.getTitle();
        this.description = proposal.getDescription();
        addMember(proposal.getAdvisor(), ProjectRole.ADVISOR);
        addMember(proposal.getStudent(), ProjectRole.STUDENT);
    }

    public void finish() {
        ensureActive();
        status = ProjectStatus.FINISHED;
    }

    public void cancel() {
        ensureActive();
        status = ProjectStatus.CANCELLED;
    }

    public boolean isActive() {
        return status == ProjectStatus.ACTIVE;
    }

    private void ensureActive() {
        if (status != ProjectStatus.ACTIVE) {
            throw new IllegalStateException("Project is not active.");
        }
    }

    public ProjectMember addMember(User user, ProjectRole role) {
        boolean alreadyMember = members.stream()
                .anyMatch(m -> m.getUser().getId().equals(user.getId()));
        if (alreadyMember) {
            throw new IllegalArgumentException("User is already a member of this project.");
        }
        ProjectMember member = new ProjectMember(this, user, role);
        members.add(member);
        return member;
    }

    public void removeMember(User user) {
        members.removeIf(m -> m.getUser().getId().equals(user.getId()));
    }

    public boolean hasMember(User user) {
        return hasMember(user.getId());
    }

    public boolean hasMember(Long userId) {
        return members.stream().anyMatch(m -> m.getUser().getId().equals(userId));
    }

    public void attachChat(Chat chat) {
        this.chat = chat;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Proposal getProposal() {
        return proposal;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<ProjectMember> getMembers() {
        return members;
    }

    public Chat getChat() {
        return chat;
    }
}
