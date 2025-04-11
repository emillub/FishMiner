package com.github.FishMiner.domain.ports.in;

import com.badlogic.gdx.Screen;
import com.github.FishMiner.domain.GameContext;
import com.github.FishMiner.ui.ports.out.ScreenType;

public interface IScreenFactory {
    Screen getScreen(ScreenType type, GameContext gameContext);
}
