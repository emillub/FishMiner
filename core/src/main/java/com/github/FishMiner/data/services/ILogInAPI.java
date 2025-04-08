package com.github.FishMiner.data.services;

public interface ILogInAPI {
    void login(String email, String password, FirebaseAuthCallback callback);
    void register(String email, String password, FirebaseAuthCallback callback);
    String getCurrentUsername();

    default void logout() {

    }
}
