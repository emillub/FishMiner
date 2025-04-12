package com.github.FishMiner.domain.ports.out;


public interface ILoginHandler {
    void login(String email, String password, FirebaseAuthCallback callback);
}
