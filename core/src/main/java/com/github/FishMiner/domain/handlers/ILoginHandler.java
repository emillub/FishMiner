package com.github.FishMiner.domain.handlers;


import com.github.FishMiner.domain.ports.out.FirebaseAuthCallback;

public interface ILoginHandler {
    void login(String email, String password, FirebaseAuthCallback callback);
}
