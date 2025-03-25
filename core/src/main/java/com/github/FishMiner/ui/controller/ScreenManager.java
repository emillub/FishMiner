package com.github.FishMiner.ui.controller;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.github.FishMiner.FishMinerGame;
import com.github.FishMiner.ui.LoginScreen;
import com.github.FishMiner.ui.MenuScreen;
import com.github.FishMiner.ui.PlayScreen;

public class ScreenManager {
    private static ScreenManager instance;
    private FishMinerGame game;

    private Screen menuScreen;
    private Screen playScreen;
    private Screen pauseScreen;
    private Screen settingScreen;

    private ScreenManager(FishMinerGame game) {
        this.game = game;
        this.menuScreen = new MenuScreen();
        // Initialize more screens here
    }

    public static ScreenManager getInstance(FishMinerGame game) {
        if (instance == null) {
            instance = new ScreenManager(game);
        }
        return instance;
    }

    public static ScreenManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ScreenManager is not initialized. Initialize with getInstance(Game game) first.");
        }
        return instance;
    }

    public FishMinerGame getGame() {
        return game;
    }

    public void showMenu() {
        game.setScreen(menuScreen);
    }

    public void startGamePressed() {
        // Handle game start logic here
        if (playScreen == null) {
            playScreen = new PlayScreen();
        }
        game.setScreen(playScreen);
    }

    public void setSettingScreen(Screen screen){
        game.setScreen(screen);
    }

    public void setLoginScreen(LoginScreen loginScreen) {
        game.setScreen(loginScreen);
    }

    public void pauseGamePressed() {
        game.setScreen(pauseScreen);
    }

    public void resumeGamePresed() {
        game.setScreen(playScreen);
    }
}
