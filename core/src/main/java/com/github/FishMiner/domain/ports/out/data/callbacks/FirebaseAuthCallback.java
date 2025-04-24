package com.github.FishMiner.domain.ports.out.data.callbacks;

public interface FirebaseAuthCallback {
    void onSuccess();
    void onFailure(String errorMessage);
}
