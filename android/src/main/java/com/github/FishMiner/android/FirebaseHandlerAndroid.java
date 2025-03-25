package com.github.FishMiner.android;

import com.github.FishMiner.data.ports.out.FirebaseAuthCallback;
import com.github.FishMiner.data.ports.out.FirebaseInterface;
import com.google.firebase.auth.FirebaseAuth;
public class FirebaseHandlerAndroid implements FirebaseInterface {
    private FirebaseAuth auth;

    public FirebaseHandlerAndroid() {
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void login(String email, String password, FirebaseAuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure(task.getException().getMessage());
                }
            });
    }

    @Override
    public void register(String email, String password, FirebaseAuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure(task.getException().getMessage());
                }
            });
    }
}
