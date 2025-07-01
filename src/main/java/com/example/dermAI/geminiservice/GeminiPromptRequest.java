package com.example.dermAI.geminiservice;

public class GeminiPromptRequest {
    private String prompt;
    private String image; // base64 string

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
