package com.github.FishMiner.domain.ports.out.data.interfaces.interfaces;


import com.github.FishMiner.domain.ports.out.data.callbacks.callbacks.FirebaseAuthCallback;

public interface ILoginHandler {
    void login(String email, String password, FirebaseAuthCallback callback);
}
