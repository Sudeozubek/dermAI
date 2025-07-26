package com.example.dermAI.dao.Blog;

import com.example.dermAI.entity.Blog.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByPostId(UUID postId);

    void deleteById(UUID id);
}
