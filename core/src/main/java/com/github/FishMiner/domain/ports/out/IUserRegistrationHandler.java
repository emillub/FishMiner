package com.github.FishMiner.domain.ports.out;

import com.github.FishMiner.domain.ports.in.data.FirebaseAuthCallback;

public interface IUserRegistrationHandler {
    void register(String email, String password, FirebaseAuthCallback callback);
}
