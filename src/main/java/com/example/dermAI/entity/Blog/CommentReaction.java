package com.example.dermAI.entity.Blog;

import com.example.dermAI.entity.User;
import com.example.dermAI.enums.ReactionType;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

import static jakarta.persistence.FetchType.LAZY;


@Entity
@Table(name="comment_reaction")
public class CommentReaction {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = LAZY)
    private User user;

    @ManyToOne(fetch = LAZY)
    private Comment comment;

    @Enumerated(EnumType.STRING)
    private ReactionType type;

    public CommentReaction(User user, Comment comment) {
    }

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

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public ReactionType getType() {
        return type;
    }

    public void setType(ReactionType type) {
        this.type = type;
    }
}