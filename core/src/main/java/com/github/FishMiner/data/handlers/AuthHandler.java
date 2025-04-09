package com.github.FishMiner.data.handlers;

import com.github.FishMiner.data.ports.out.IAuthService;
import com.github.FishMiner.domain.handlers.IAuthHandler;

public class AuthHandler implements IAuthHandler {
    private final IAuthService authService;

    public AuthHandler(IAuthService authService) {
        this.authService = authService;
    }

    @Override
    public void onAuthSuccess() {

    }

    @Override
    public void onAuthFailure(String error) {

    }
}
