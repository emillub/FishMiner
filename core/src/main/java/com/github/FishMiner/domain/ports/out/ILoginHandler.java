package com.github.FishMiner.domain.ports.out;


import com.github.FishMiner.domain.ports.in.data.FirebaseAuthCallback;

public interface ILoginHandler {
    void login(String email, String password, FirebaseAuthCallback callback);
}
