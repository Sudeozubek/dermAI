package com.example.dermAI.controller.Blog;

import com.example.dermAI.dto.Blog.request.CommentRequest;
import com.example.dermAI.dto.Blog.request.PostRequest;
import com.example.dermAI.dto.Blog.request.ReactionRequest;
import com.example.dermAI.dto.Blog.response.CommentResponse;
import com.example.dermAI.dto.Blog.response.PostResponse;
import com.example.dermAI.dto.Blog.response.ReactionResponse;
import com.example.dermAI.service.Blog.BlogService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/blog")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @ModelAttribute("posts")
    public Page<PostResponse> populatePosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return blogService.listPosts(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @GetMapping
    public String viewBlog(Model model) {
        model.addAttribute("postRequest", new PostRequest());
        return "blog/blog";
    }

    @PostMapping(path = "/posts", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @PreAuthorize("isAuthenticated()")
    public String createPostForm(@AuthenticationPrincipal(expression = "username") String username, @Valid @ModelAttribute("postRequest") PostRequest postRequest, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "blog/blog";
        }

        blogService.createPost(username, postRequest);
        redirectAttributes.addFlashAttribute("successMessage", "Yazınız başarıyla yayınlandı!");
        return "redirect:/blog";
    }

    @PostMapping("/posts/{id}/delete")
    @PreAuthorize("#username == principal.username")
    public String deletePost(@AuthenticationPrincipal(expression = "username") String username, @PathVariable UUID id, RedirectAttributes flash) {
        blogService.deletePost(username, id);
        flash.addFlashAttribute("successMessage", "Yazı başarıyla silindi!");
        return "redirect:/blog";
    }

    @GetMapping("/posts/{id}")
    public PostResponse getOne(@PathVariable UUID id) {
        return blogService.getPost(id);
    }

    @PostMapping("/posts/{id}/comments")
    public CommentResponse comment(@AuthenticationPrincipal(expression = "username") String username, @PathVariable UUID id, @RequestBody CommentRequest req) {
        return blogService.addComment(username, id, req);
    }

    @PostMapping("/posts/{id}/reactions")
    public ReactionResponse react(@AuthenticationPrincipal(expression = "username") String username, @PathVariable UUID id, @RequestBody ReactionRequest req) {
        return blogService.react(username, id, req);
    }
}
