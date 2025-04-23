package com.github.FishMiner.data.handlers;

import com.github.FishMiner.data.ports.out.IAuthService;
import com.github.FishMiner.domain.ports.out.data.callbacks.callbacks.FirebaseAuthCallback;
import com.github.FishMiner.domain.ports.out.data.interfaces.interfaces.IUserRegistrationHandler;

public class UserRegistrationHandler implements IUserRegistrationHandler {
    private IAuthService authService;
    public UserRegistrationHandler(IAuthService authService) {
        this.authService = authService;
    }
    @Override
    public void register(String email, String password, FirebaseAuthCallback callback) {
        authService.register(email, password, new FirebaseAuthCallback() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onFailure(String errorMessage) {
                callback.onFailure(errorMessage);
            }
        });
    }
}
