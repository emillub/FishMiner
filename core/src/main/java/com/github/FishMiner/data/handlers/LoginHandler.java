package com.github.FishMiner.data.handlers;

import com.github.FishMiner.data.services.FirebaseAuthCallback;
import com.github.FishMiner.data.ports.out.IAuthService;
import com.github.FishMiner.domain.handlers.LoginCallBack;
import com.github.FishMiner.domain.handlers.ILoginHandler;

public class LoginHandler implements ILoginHandler {
    private final IAuthService authService;

    public LoginHandler(IAuthService authService) {
        this.authService = authService;
    }

    @Override
    public void login(String email, String password, LoginCallBack callback) {
        authService.login(email, password, new FirebaseAuthCallback() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }
}
