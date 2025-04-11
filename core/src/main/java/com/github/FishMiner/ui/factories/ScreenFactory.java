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
    public ScreenFactory() {
    }

    @Override
    public Screen getScreen(ScreenType type, GameContext gameContext) {
        switch (type) {
            case PLAY -> {
                return new PlayScreen(gameContext);
            }
            case MENU -> {
                return new MenuScreen(gameContext);
            }
            case LOGIN -> {
                return new LoginScreen(gameContext);
            }
            case LEADERBOARD -> {
                return new LeaderBoardScreen(gameContext);
            }
            case LEVEL_COMPLETE -> {
                return new LevelCompleteScreen(gameContext);
            }
            case LEVEL_LOST -> {
                return new LevelLostScreen(gameContext);
            }
            case SETTINGS -> {
                return new SettingScreen(gameContext);
            }
            case UPGRADE -> {
                return new UpgradeScreen(gameContext);
            }
        }
    }
}
