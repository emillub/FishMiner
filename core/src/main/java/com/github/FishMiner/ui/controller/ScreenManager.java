package com.github.FishMiner.ui.controller;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.github.FishMiner.FishMinerGame;
import com.github.FishMiner.Logger;
import com.github.FishMiner.domain.ecs.components.InventoryComponent;

import com.github.FishMiner.domain.ecs.util.ValidateUtil;
import com.github.FishMiner.domain.ports.in.IGameEventBus;
import com.github.FishMiner.ui.LeaderBoardScreen;
import com.github.FishMiner.ui.LevelCompleteScreen;
import com.github.FishMiner.ui.LevelLostScreen;
import com.github.FishMiner.ui.LoginScreen;
import com.github.FishMiner.ui.MenuScreen;
import com.github.FishMiner.ui.PlayScreen;
import com.github.FishMiner.ui.UpgradeScreen;

public class ScreenManager {
    private final static String TAG = "ScreenManager";
    private static ScreenManager instance;
    private final FishMinerGame game;
    private final Screen menuScreen;
    private Screen playScreen;
    private Screen pauseScreen;
    private Screen settingScreen;
    private Screen upgradeScreen;
    private InventoryComponent currentInventory;


    private ScreenManager(FishMinerGame game) {
        this.game = game;
        this.menuScreen = new MenuScreen();
        this.currentInventory = new InventoryComponent();
    }

    public static ScreenManager getInstance(FishMinerGame game) {
        if (instance == null) {
            instance = new ScreenManager(game);
        }
        return instance;
    }

    public static ScreenManager getInstance() {
        try {
            ValidateUtil.validateNotNull(instance, "ScreenManager.instance");
            return instance;
        } catch (IllegalArgumentException e) {
            IllegalStateException exception = new IllegalStateException("ScreenManager cannot be null");
            Logger.getInstance().error(TAG, "ScreenManager is not initialized. Initialize with getInstance(Game game) first.", exception);
            throw exception;
        }
    }

    public FishMinerGame getGame() {
        return game;
    }

    public void showMenu() {
        game.setScreen(menuScreen);
    }

    public void startGamePressed() {
        // Handle game start logic here
        if (currentInventory == null) {
            Logger.getInstance().error(TAG, "currentInventory was null in ScreenManager — creating new one.");
            currentInventory = new InventoryComponent();
        }
        if (playScreen == null) {
            playScreen = new PlayScreen(1, currentInventory);
        }

        game.setScreen(playScreen);
    }

    public void setSettingScreen(Screen screen){
        game.setScreen(screen);
    }

    public void setLoginScreen(LoginScreen loginScreen) {
        game.setScreen(loginScreen);
    }
    public void setLeaderBoardScreen(LeaderBoardScreen leaderBoardScreen){
        game.setScreen(leaderBoardScreen);
    }

    public void showLevelCompleteScreen(int levelNumber, InventoryComponent inventory) {
        this.currentInventory = inventory;
        game.setScreen(new LevelCompleteScreen(levelNumber, inventory));
    }

    public void startNextLevel(int nextLevel, float previousScore, InventoryComponent inventory) {
        this.currentInventory = inventory;
        game.setScreen(new PlayScreen(nextLevel, inventory));
    }

    public void showLevelLostScreen() {
        LevelLostScreen lostScreen = new LevelLostScreen();
        game.setScreen(lostScreen);
    }

    public void showUpgradeScreen(int nextLevel, InventoryComponent inventory){
        this.currentInventory = inventory;
        game.setScreen(new UpgradeScreen(nextLevel, inventory));
    }

    public InventoryComponent getCurrentInventory() {
        return currentInventory;
    }

    public void pauseGamePressed() {
        game.setScreen(pauseScreen);
    }

    public void resumeGamePressed() {
        game.setScreen(playScreen);
    }
}
