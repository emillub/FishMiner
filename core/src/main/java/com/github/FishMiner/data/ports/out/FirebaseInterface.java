package com.github.FishMiner.data.ports.out;

public interface FirebaseInterface{
    void login(String email, String password, FirebaseAuthCallback callback);
    void register(String email, String password, FirebaseAuthCallback callback);
}
