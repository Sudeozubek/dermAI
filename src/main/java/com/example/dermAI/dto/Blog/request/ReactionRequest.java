package com.example.dermAI.dto.Blog.request;

import com.example.dermAI.enums.ReactionType;

public class ReactionRequest {
    private ReactionType type;

    public ReactionType getType() {
        return type;
    }

    public void setType(ReactionType type) {
        this.type = type;
    }
}
