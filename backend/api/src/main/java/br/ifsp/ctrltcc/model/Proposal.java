package br.ifsp.ctrltcc.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Proposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 5000, nullable = false)
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private User student;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private User advisor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProposalStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime answeredAt;

    @Column(length = 2000)
    private String advisorFeedback;

    @PrePersist
    private void prePersist() {
        createdAt = LocalDateTime.now();
        status = ProposalStatus.PENDING;
    }

    protected Proposal() {
    }

    public Proposal(
            String title,
            String description,
            User student,
            User advisor) {
        this.title = title;
        this.description = description;
        this.student = student;
        this.advisor = advisor;
    }

    public void accept() {
        ensureRespondable();
        status = ProposalStatus.ACCEPTED;
        answeredAt = LocalDateTime.now();
    }

    public void reject(String feedback) {
        ensureRespondable();
        status = ProposalStatus.REJECTED;
        answeredAt = LocalDateTime.now();
        this.advisorFeedback = feedback;
    }

    public void requestChanges(String feedback) {
        ensureRespondable();
        if (feedback == null || feedback.isBlank()) {
            throw new IllegalArgumentException("Feedback is required when requesting changes.");
        }
        status = ProposalStatus.CHANGES_REQUESTED;
        answeredAt = LocalDateTime.now();
        this.advisorFeedback = feedback;
    }

    public void resubmit(String newTitle, String newDescription) {
        if (status != ProposalStatus.CHANGES_REQUESTED) {
            throw new IllegalStateException("Only proposals with changes requested can be resubmitted.");
        }
        this.title = newTitle;
        this.description = newDescription;
        this.status = ProposalStatus.PENDING;
        this.answeredAt = null;
        this.advisorFeedback = null;
    }

    private void ensureRespondable() {
        if (status != ProposalStatus.PENDING && status != ProposalStatus.CHANGES_REQUESTED) {
            throw new IllegalStateException("Proposal already processed.");
        }
    }

    public boolean isPending() {
        return status == ProposalStatus.PENDING;
    }

    public boolean isAccepted() {
        return status == ProposalStatus.ACCEPTED;
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

    public User getStudent() {
        return student;
    }

    public User getAdvisor() {
        return advisor;
    }

    public ProposalStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getAnsweredAt() {
        return answeredAt;
    }

    public String getAdvisorFeedback() {
        return advisorFeedback;
    }
}
