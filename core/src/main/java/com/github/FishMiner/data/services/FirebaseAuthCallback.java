package com.github.FishMiner.data.services;

public interface FirebaseAuthCallback {
    void onSuccess();
    void onFailure(String errorMessage);
}
