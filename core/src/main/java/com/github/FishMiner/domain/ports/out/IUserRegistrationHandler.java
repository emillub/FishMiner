package com.github.FishMiner.domain.ports.out;

public interface IUserRegistrationHandler {
    void register(String email, String password, FirebaseAuthCallback callback);
}
