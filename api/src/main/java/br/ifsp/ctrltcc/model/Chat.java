package br.ifsp.ctrltcc.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "chat__members",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Chat() {}

    public Chat(String name, String description, User createdBy) {
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
        this.members.add(createdBy);
    }

    public Long getId() { 
    	return id; 
	}

    public String getName() { 
    	return name;
    }
    public void setName(String name) { 
    	this.name = name; 
	}

    public String getDescription() { 
    	return description; 
	}
    public void setDescription(String description) { 
    	this.description = description; 
    }

    public LocalDateTime getCreatedAt() { 
    	return createdAt; 
    }

    public User getCreatedBy() { 
    	return createdBy; 
    }

    public Set<User> getMembers() { 
    	return members; 
    }

    public void addMember(User user) { 
    	this.members.add(user); 
    }
    public void removeMember(User user) { 
    	this.members.remove(user); 
    }
    public boolean hasMember(User user) { 
    	return this.members.contains(user); 
    }
}
