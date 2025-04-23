package com.github.FishMiner.domain.ports.out.data.interfaces.interfaces;

import com.github.FishMiner.domain.ports.out.data.callbacks.callbacks.FirebaseAuthCallback;

public interface IUserRegistrationHandler {
    void register(String email, String password, FirebaseAuthCallback callback);
}
