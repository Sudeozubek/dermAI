package com.example.dermAI.mapper.Blog;

import com.example.dermAI.dao.Blog.ReactionRepository;
import com.example.dermAI.dto.Blog.request.PostRequest;
import com.example.dermAI.dto.Blog.response.CommentResponse;
import com.example.dermAI.dto.Blog.response.PostResponse;
import com.example.dermAI.entity.Blog.Comment;
import com.example.dermAI.entity.Blog.Post;
import com.example.dermAI.enums.ReactionType;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        imports = { ReactionType.class }
)
public interface BlogMapper {

    // @Mapping(target = "author.username", source = "authorUsername")
    Post toPostEntity(PostRequest dto);

    @Mapping(target = "authorUsername", source = "post.author.username")
    @Mapping(target = "likeCount", expression = "java(reactionRepo.countByPostIdAndType(post.getId(), com.example.dermAI.enums.ReactionType.LIKE))")
    @Mapping(target = "dislikeCount", expression = "java(reactionRepo.countByPostIdAndType(post.getId(), com.example.dermAI.enums.ReactionType.DISLIKE))")
    PostResponse toPostResponse(Post post, @Context ReactionRepository reactionRepo);

    @Mapping(target = "authorUsername", source = "comment.author.username")
    CommentResponse toCommentResponse(Comment comment);
}

