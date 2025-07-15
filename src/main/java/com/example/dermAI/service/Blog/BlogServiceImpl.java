package com.example.dermAI.service.Blog;

import com.example.dermAI.dao.Blog.CommentRepository;
import com.example.dermAI.dao.Blog.PostRepository;
import com.example.dermAI.dao.Blog.ReactionRepository;
import com.example.dermAI.dao.UserRepository;
import com.example.dermAI.dto.Blog.request.CommentRequest;
import com.example.dermAI.dto.Blog.request.PostRequest;
import com.example.dermAI.dto.Blog.request.ReactionRequest;
import com.example.dermAI.dto.Blog.response.CommentResponse;
import com.example.dermAI.dto.Blog.response.PostResponse;
import com.example.dermAI.dto.Blog.response.ReactionResponse;
import com.example.dermAI.entity.Blog.Comment;
import com.example.dermAI.entity.Blog.Post;
import com.example.dermAI.entity.Blog.Reaction;
import com.example.dermAI.entity.User;
import com.example.dermAI.enums.ReactionType;
import com.example.dermAI.mapper.Blog.BlogMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class BlogServiceImpl implements BlogService {

    private final PostRepository postRepo;
    private final CommentRepository commentRepo;
    private final ReactionRepository reactionRepo;
    private final UserRepository userRepo;
    private final BlogMapper mapper;

    public BlogServiceImpl(PostRepository postRepo, CommentRepository commentRepo, ReactionRepository reactionRepo, UserRepository userRepo, BlogMapper mapper) {
        this.postRepo = postRepo;
        this.commentRepo = commentRepo;
        this.reactionRepo = reactionRepo;
        this.userRepo = userRepo;
        this.mapper = mapper;
    }

    @Override
    public Page<PostResponse> listPosts(Pageable pageable) {
        return postRepo.findAll(pageable).map(post -> mapper.toPostResponse(post, reactionRepo));
    }

    @Override
    @Transactional
    public PostResponse createPost(String username, PostRequest req) {
        User user = userRepo.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Post post = mapper.toPostEntity(req);
        post.setAuthor(user);
        post = postRepo.save(post);
        return mapper.toPostResponse(post, reactionRepo);
    }

    public PostResponse getPost(UUID postId) {
        Post post = postRepo.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        return mapper.toPostResponse(post, reactionRepo);
    }

    @Override
    @Transactional
    public CommentResponse addComment(String username, UUID postId, CommentRequest req) {
        User user = userRepo.findByUsername(username).orElseThrow();
        Post post = postRepo.findById(postId).orElseThrow();
        Comment c = new Comment();
        c.setAuthor(user);
        c.setPost(post);
        c.setContent(req.getContent());
        Comment saved = commentRepo.save(c);
        return mapper.toCommentResponse(saved);
    }

    @Override
    @Transactional
    public ReactionResponse react(String username, UUID postId, ReactionRequest req) {
        User user = userRepo.findByUsername(username).orElseThrow();
        Post post = postRepo.findById(postId).orElseThrow();
        Reaction reaction = reactionRepo.findByUserIdAndPostId(user.getId(), post.getId()).orElseGet(() -> new Reaction());
        reaction.setUser(user);
        reaction.setPost(post);
        reaction.setType(req.getType());
        reactionRepo.save(reaction);
        long likes = reactionRepo.countByPostIdAndType(post.getId(), ReactionType.LIKE);
        long dislikes = reactionRepo.countByPostIdAndType(post.getId(), ReactionType.DISLIKE);
        return new ReactionResponse(req.getType(), likes, dislikes);
    }
}
