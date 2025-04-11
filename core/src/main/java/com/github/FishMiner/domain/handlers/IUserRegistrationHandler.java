package com.github.FishMiner.domain.handlers;

import com.github.FishMiner.domain.ports.out.FirebaseAuthCallback;

public interface IUserRegistrationHandler {
    void register(String email, String password, FirebaseAuthCallback callback);
}
