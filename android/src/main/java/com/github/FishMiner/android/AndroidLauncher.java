package com.github.FishMiner.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.github.czyzby.autumn.android.scanner.AndroidClassScanner;
import com.github.czyzby.autumn.mvc.application.AutumnApplication;
import com.github.FishMiner.FishMinerGame;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true; // Recommended, but not required.
        initialize(new AutumnApplication(new AndroidClassScanner(), FishMinerGame.class), configuration);
    }
}