package com.github.FishMiner.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.github.FishMiner.FishMinerGame;
import com.github.FishMiner.android.infrastructure.AndroidAuthService;
import com.github.FishMiner.android.infrastructure.AndroidLeaderboardService;
import com.google.firebase.FirebaseApp;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true; // Recommended, but not required.

        AndroidAuthService firebaseHandlerAndroid = new AndroidAuthService();
        AndroidLeaderboardService leaderboardHandler = new AndroidLeaderboardService();
        initialize(new FishMinerGame(firebaseHandlerAndroid, leaderboardHandler), configuration);
    }
}
