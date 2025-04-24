package com.github.FishMiner.domain.ports.out.data.interfaces;

import com.github.FishMiner.domain.ports.out.data.callbacks.FirebaseAuthCallback;

public interface IUserRegistrationHandler {
    void register(String email, String password, FirebaseAuthCallback callback);
}
