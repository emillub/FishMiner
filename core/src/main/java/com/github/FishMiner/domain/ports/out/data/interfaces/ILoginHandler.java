package com.github.FishMiner.domain.ports.out.data.interfaces;


import com.github.FishMiner.domain.ports.out.data.callbacks.FirebaseAuthCallback;

public interface ILoginHandler {
    void login(String email, String password, FirebaseAuthCallback callback);
}
