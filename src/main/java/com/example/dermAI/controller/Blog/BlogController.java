package com.example.dermAI.controller.Blog;

import com.example.dermAI.dto.Blog.request.CommentRequest;
import com.example.dermAI.dto.Blog.request.PostRequest;
import com.example.dermAI.dto.Blog.request.ReactionRequest;
import com.example.dermAI.dto.Blog.response.CommentResponse;
import com.example.dermAI.dto.Blog.response.PostResponse;
import com.example.dermAI.dto.Blog.response.ReactionResponse;
import com.example.dermAI.service.Blog.BlogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/blog/posts")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping
    public Page<PostResponse> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return blogService.listPosts(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @PostMapping
    public ResponseEntity<PostResponse> create(@AuthenticationPrincipal(expression = "username") String username, @RequestBody PostRequest req) {
        PostResponse resp = blogService.createPost(username, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping("/{id}")
    public PostResponse getOne(@PathVariable UUID id) {
        return blogService.getPost(id);
    }

    @PostMapping("/{id}/comments")
    public CommentResponse comment(@AuthenticationPrincipal(expression = "username") String username, @PathVariable UUID id, @RequestBody CommentRequest req) {
        return blogService.addComment(username, id, req);
    }

    @PostMapping("/{id}/reactions")
    public ReactionResponse react(@AuthenticationPrincipal(expression = "username") String username, @PathVariable UUID id, @RequestBody ReactionRequest req) {
        return blogService.react(username, id, req);
    }
}
