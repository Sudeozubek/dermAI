package com.example.dermAI.controller.User.contract;

import com.example.dermAI.dto.User.request.RegisterRequest;

public interface UserContract {

    void registerUser(RegisterRequest registerRequest);
}
