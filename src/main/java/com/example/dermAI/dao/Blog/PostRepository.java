package com.example.dermAI.dao.Blog;

import com.example.dermAI.entity.Blog.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
}
