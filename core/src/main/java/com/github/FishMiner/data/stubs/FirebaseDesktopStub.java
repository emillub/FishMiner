package com.github.FishMiner.data.stubs;

import com.github.FishMiner.data.ports.out.FirebaseInterface;
import com.github.FishMiner.data.ports.out.FirebaseAuthCallback;

public class FirebaseDesktopStub implements FirebaseInterface {

    @Override
    public void login(String email, String password, FirebaseAuthCallback callback) {
        System.out.println("Desktop login stub called for: " + email);
        callback.onSuccess();
    }

    @Override
    public void register(String email, String password, FirebaseAuthCallback callback) {
        System.out.println("Desktop register stub called for: " + email);
        callback.onSuccess();
    }
}
