package com.github.FishMiner.domain.ports.in;

import com.badlogic.gdx.Screen;
import com.github.FishMiner.ui.ports.out.IGameContext;
import com.github.FishMiner.ui.ports.out.ScreenType;
import com.github.FishMiner.ui.screens.AbstractScreen;

public interface IScreenFactory {
    AbstractScreen getScreen(ScreenType type, IGameContext gameContext);
}
