package com.example.dermAI.service.Blog;

import com.example.dermAI.dao.Blog.CommentReactionRepository;
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
import com.example.dermAI.entity.Blog.CommentReaction;
import com.example.dermAI.entity.Blog.Post;
import com.example.dermAI.entity.Blog.Reaction;
import com.example.dermAI.entity.User;
import com.example.dermAI.enums.ReactionType;
import com.example.dermAI.mapper.Blog.BlogMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class BlogServiceImpl implements BlogService {

    private final PostRepository postRepo;
    private final CommentRepository commentRepo;
    private final CommentReactionRepository commentReactionRepo;
    private final ReactionRepository reactionRepo;
    private final UserRepository userRepo;
    private final BlogMapper mapper;

    public BlogServiceImpl(PostRepository postRepo, CommentRepository commentRepo, CommentReactionRepository commentReactionRepo, ReactionRepository reactionRepo, UserRepository userRepo, BlogMapper mapper) {
        this.postRepo = postRepo;
        this.commentRepo = commentRepo;
        this.commentReactionRepo = commentReactionRepo;
        this.reactionRepo = reactionRepo;
        this.userRepo = userRepo;
        this.mapper = mapper;
    }

    @Override
    public Page<PostResponse> listPosts(Pageable pageable) {
        Page<PostResponse> page = postRepo.findAll(pageable).map(post -> mapper.toPostResponse(post, reactionRepo));

        // her yorum için commentReactionRepo’dan sayıları alıp DTO’ya koy
        page.getContent().forEach(postResp -> postResp.getComments().forEach(cResp -> {
            long likes = commentReactionRepo.countByComment_IdAndType(cResp.getId(), ReactionType.LIKE);
            long dislikes = commentReactionRepo.countByComment_IdAndType(cResp.getId(), ReactionType.DISLIKE);
            cResp.setLikeCount(likes);
            cResp.setDislikeCount(dislikes);
        }));

        return page;
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

    @Override
    public void deletePost(String username, UUID postId) {
        Post post = postRepo.findById(postId).orElseThrow(() -> new NoSuchElementException("Post bulunamadı"));
        if (!post.getAuthor().getUsername().equals(username)) {
            throw new AccessDeniedException("Bu işlemi yapmaya yetkiniz yok");
        }
        postRepo.delete(post);
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
        User user = userRepo.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User bulunamadı"));
        Post post = postRepo.findById(postId).orElseThrow(() -> new NoSuchElementException("Post bulunamadı"));

        // Eğer kullanıcı zaten tepki verdiyse güncelle, yoksa yeni yarat
        Reaction reaction = reactionRepo.findByUserIdAndPostId(user.getId(), post.getId()).orElseGet(Reaction::new);

        reaction.setUser(user);
        reaction.setPost(post);
        reaction.setType(req.getType());
        reactionRepo.save(reaction);

        long likes = reactionRepo.countByPostIdAndType(postId, ReactionType.LIKE);
        long dislikes = reactionRepo.countByPostIdAndType(postId, ReactionType.DISLIKE);

        return new ReactionResponse(req.getType(), likes, dislikes);
    }

    @Override
    @Transactional
    public void reactToComment(String username, UUID postId, UUID commentId, ReactionType type) {
        User user = userRepo.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User yok"));
        Comment comment = commentRepo.findById(commentId).orElseThrow(() -> new NoSuchElementException("Comment bulunamadı"));

        if (!comment.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("Bu yorum bu gönderiye ait değil");
        }

        CommentReaction cr = commentReactionRepo.findByUserAndComment(user, comment).orElseGet(() -> new CommentReaction(user, comment));

        cr.setType(type);
        commentReactionRepo.save(cr);
        commentReactionRepo.flush();
    }

    @Override
    @Transactional
    public void deleteComment(String username, UUID postId, UUID commentId) {
        Comment comment = commentRepo.findById(commentId).orElseThrow(() -> new NoSuchElementException("Yorum bulunamadı"));

        if (!comment.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("Bu yorum bu gönderiye ait değil");
        }

        boolean isPostAuthor = comment.getPost().getAuthor().getUsername().equals(username);
        boolean isCommentAuthor = comment.getAuthor().getUsername().equals(username);

        if (!isPostAuthor && !isCommentAuthor) {
            throw new AccessDeniedException("Yorum silme yetkiniz yok");
        }

        // güvenlik kontrolleri…
        Post p = comment.getPost();
        p.getComments().remove(comment);           // collection’dan da at
        commentRepo.delete(comment);               // sil
        commentRepo.flush();
    }

    /**
     * SpEL’den çağrılacak metot.
     *
     * @PreAuthorize içinde @blogService.isPostAuthor(...) diye kullanacağız.
     */
    @Override
    public boolean isPostAuthor(String username, UUID postId) {
        return postRepo.findById(postId).map(p -> p.getAuthor().getUsername().equals(username)).orElse(false);
    }
}
