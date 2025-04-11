package com.github.FishMiner.domain.ports.out;

public interface FirebaseAuthCallback {
    void onSuccess();
    void onFailure(String errorMessage);
}
