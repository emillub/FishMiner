package com.github.FishMiner.data.ports.out;

public interface FirebaseAuthCallback {
    void onSuccess();
    void onFailure(String errorMessage);
}
