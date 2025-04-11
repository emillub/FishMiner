package com.github.FishMiner.ui.factories;

import com.badlogic.gdx.Screen;
import com.github.FishMiner.domain.GameContext;
import com.github.FishMiner.domain.ports.in.IScreenFactory;
import com.github.FishMiner.ui.ports.out.ScreenType;
import com.github.FishMiner.ui.screens.LeaderBoardScreen;
import com.github.FishMiner.ui.screens.LevelCompleteScreen;
import com.github.FishMiner.ui.screens.LevelLostScreen;
import com.github.FishMiner.ui.screens.LoginScreen;
import com.github.FishMiner.ui.screens.MenuScreen;
import com.github.FishMiner.ui.screens.PlayScreen;
import com.github.FishMiner.ui.screens.SettingScreen;
import com.github.FishMiner.ui.screens.UpgradeScreen;

public class ScreenFactory implements IScreenFactory {
    private static final String TAG = "ScreenFactory";

    public ScreenFactory() {
    }

    @Override
    public Screen getScreen(ScreenType type, GameContext gameContext) {
        Screen newScreen;
        switch (type) {
            case PLAY -> newScreen = new PlayScreen(gameContext);
            case MENU -> newScreen = new MenuScreen(gameContext);
            case LOGIN -> newScreen = new LoginScreen(gameContext);
            case LEADERBOARD -> newScreen = new LeaderBoardScreen(gameContext);
            case LEVEL_COMPLETE -> newScreen = new LevelCompleteScreen(gameContext);
            case LEVEL_LOST -> newScreen = new LevelLostScreen(gameContext);
            case SETTINGS -> newScreen = new SettingScreen(gameContext);
            case UPGRADE -> newScreen = new UpgradeScreen(gameContext);
            default ->
                throw new IllegalArgumentException(TAG + "No screen exists for type: " + type);
        };
        return newScreen;
    }
}
