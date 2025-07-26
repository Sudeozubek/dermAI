package com.example.dermAI.dao.Blog;

import com.example.dermAI.entity.Blog.Comment;
import com.example.dermAI.entity.Blog.CommentReaction;
import com.example.dermAI.entity.User;
import com.example.dermAI.enums.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, UUID> {

    Optional<CommentReaction> findByUserAndComment(User u, Comment c);

    long countByComment_IdAndType(UUID commentId, ReactionType type);
}
