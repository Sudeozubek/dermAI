package com.example.dermAI.service.Blog;

import com.example.dermAI.dto.Blog.request.CommentRequest;
import com.example.dermAI.dto.Blog.request.PostRequest;
import com.example.dermAI.dto.Blog.request.ReactionRequest;
import com.example.dermAI.dto.Blog.response.CommentResponse;
import com.example.dermAI.dto.Blog.response.PostResponse;
import com.example.dermAI.dto.Blog.response.ReactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BlogService {

    Page<PostResponse> listPosts(Pageable pageable);

    PostResponse createPost(String username, PostRequest req);

    PostResponse getPost(UUID postId);

    CommentResponse addComment(String username, UUID postId, CommentRequest req);

    ReactionResponse react(String username, UUID postId, ReactionRequest req);
}
