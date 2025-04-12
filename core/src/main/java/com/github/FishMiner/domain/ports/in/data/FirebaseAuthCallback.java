package com.github.FishMiner.domain.ports.in.data;

public interface FirebaseAuthCallback {
    void onSuccess();
    void onFailure(String errorMessage);
}
