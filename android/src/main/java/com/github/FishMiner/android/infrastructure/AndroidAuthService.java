package com.github.FishMiner.android.infrastructure;

import com.github.FishMiner.data.ports.out.IAuthService;
import com.github.FishMiner.domain.ports.out.data.callbacks.FirebaseAuthCallback;
import com.google.firebase.auth.FirebaseAuth;
public class AndroidAuthService implements IAuthService {
    private FirebaseAuth auth;

    public AndroidAuthService() {
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
    @Override
    public String getCurrentUsername() {
        if (auth.getCurrentUser() != null) {
            return auth.getCurrentUser().getEmail();
        } else {
            return null;
        }
    }
}
