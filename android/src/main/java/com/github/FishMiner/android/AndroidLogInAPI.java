package com.github.FishMiner.android;

import com.github.FishMiner.data.services.FirebaseAuthCallback;
import com.github.FishMiner.data.services.ILogInAPI;
import com.google.firebase.auth.FirebaseAuth;
public class AndroidLogInAPI implements ILogInAPI {
    private FirebaseAuth auth;

    public AndroidLogInAPI() {
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
