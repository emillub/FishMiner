package com.github.FishMiner.domain.ports.in.ui.interfaces;

import com.github.FishMiner.ui.ports.out.domain.interfaces.IGameContext;
import com.github.FishMiner.ui.ports.out.domain.enums.ScreenType;
import com.github.FishMiner.ui.views.AbstractScreen;

public interface IScreenFactory {
    AbstractScreen getScreen(ScreenType type, IGameContext gameContext);
}
