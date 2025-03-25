package com.github.FishMiner.ui.controller;

import com.badlogic.gdx.Screen;
import com.github.FishMiner.FishMinerGame;
import com.github.FishMiner.ui.LoginScreen;
import com.github.FishMiner.ui.MenuScreen;
import com.github.FishMiner.ui.PlayScreen;
import com.github.FishMiner.ui.SettingScreen;

public class ScreenManager {
    private static ScreenManager instance;
    private final FishMinerGame game;

    private Screen menuScreen;
    private Screen playScreen;
    private Screen pauseScreen;
    private Screen settingScreen;
    private Screen loginScreen;

    private ScreenManager(FishMinerGame game) {
        this.game = game;
        initializeScreens();
    }

    private void initializeScreens() {
        menuScreen = new MenuScreen();
        settingScreen = new SettingScreen();
        loginScreen = new LoginScreen();
        pauseScreen = null; // Initialize lazily if needed
        playScreen = null;
    }

    public static void initialize(FishMinerGame game) {
        if (instance == null) {
            instance = new ScreenManager(game);
        }
    }

    public static ScreenManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(
                    "ScreenManager not initialized. Call initialize(FishMinerGame game) first.");
        }
        return instance;
    }

    public void showMenuScreen() {
        game.setScreen(menuScreen);
    }

    public void showPlayScreen() {
        if (playScreen == null) {
            playScreen = new PlayScreen();
        }
        game.setScreen(playScreen);
    }

    public void showSettingsScreen() {
        game.setScreen(settingScreen);
    }

    public void showLoginScreen() {
        game.setScreen(loginScreen);
    }

    public void showPauseScreen() {
        if (pauseScreen == null) {
            // Initialize pause screen when needed
            pauseScreen = new MenuScreen(); // Replace with actual PauseScreen implementation
        }
        game.setScreen(pauseScreen);
    }

    public void resumeGame() {
        if (playScreen != null) {
            game.setScreen(playScreen);
        }
    }

    public void dispose() {
        if (menuScreen != null)
            menuScreen.dispose();
        if (playScreen != null)
            playScreen.dispose();
        if (pauseScreen != null)
            pauseScreen.dispose();
        if (settingScreen != null)
            settingScreen.dispose();
        if (loginScreen != null)
            loginScreen.dispose();
    }

    public FishMinerGame getGame() {
        return game;
    }
}
