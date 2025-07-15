package com.example.dermAI.dto.Blog.response;

import com.example.dermAI.enums.ReactionType;

public class ReactionResponse {
    private ReactionType type;

    private long likeCount;

    private long dislikeCount;

    public ReactionResponse(ReactionType type, long likes, long dislikes) {
    }

    public ReactionType getType() {
        return type;
    }

    public void setType(ReactionType type) {
        this.type = type;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public long getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(long dislikeCount) {
        this.dislikeCount = dislikeCount;
    }
}
