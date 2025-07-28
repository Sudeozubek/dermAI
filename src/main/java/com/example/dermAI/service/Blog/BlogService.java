package com.example.dermAI.service.Blog;

import com.example.dermAI.dto.Blog.request.CommentRequest;
import com.example.dermAI.dto.Blog.request.PostRequest;
import com.example.dermAI.dto.Blog.request.ReactionRequest;
import com.example.dermAI.dto.Blog.response.CommentResponse;
import com.example.dermAI.dto.Blog.response.PostResponse;
import com.example.dermAI.dto.Blog.response.ReactionResponse;
import com.example.dermAI.enums.ReactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BlogService {

    Page<PostResponse> listPosts(Pageable pageable);

    PostResponse createPost(String username, PostRequest req);

    void deletePost(String username, UUID postId);

    CommentResponse addComment(String username, UUID postId, CommentRequest req);

    ReactionResponse react(String username, UUID postId, ReactionRequest req);

    void reactToComment(String username, UUID postId, UUID commentId, ReactionType type);

    void deleteComment(String username, UUID postId, UUID commentId);

    /**
     * SpEL’den çağrılacak metot.
     *
     * @PreAuthorize içinde @blogService.isPostAuthor(...) diye kullanacağız.
     */
    boolean isPostAuthor(String username, UUID postId);
}
