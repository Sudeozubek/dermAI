package com.example.dermAI.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Routine {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private User user;

    @Column(columnDefinition = "TEXT")
    private String routineText;

    private LocalDateTime createdAt;

    // Getter/setter'lar...

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRoutineText() {
        return routineText;
    }

    public void setRoutineText(String routineText) {
        this.routineText = routineText;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
