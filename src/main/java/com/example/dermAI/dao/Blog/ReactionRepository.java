package com.example.dermAI.dao.Blog;

import com.example.dermAI.entity.Blog.Reaction;
import com.example.dermAI.enums.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReactionRepository extends JpaRepository<Reaction, UUID> {
    Optional<Reaction> findByUserIdAndPostId(UUID userId, UUID postId);

    long countByPostIdAndType(UUID postId, ReactionType type);
}
