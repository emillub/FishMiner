package com.github.FishMiner.data.ports.out;

import com.github.FishMiner.domain.ports.out.data.callbacks.FirebaseAuthCallback;

public interface IAuthService {
    void login(String email, String password, FirebaseAuthCallback callback);
    void register(String email, String password, FirebaseAuthCallback callback);
    String getCurrentUsername();
}
