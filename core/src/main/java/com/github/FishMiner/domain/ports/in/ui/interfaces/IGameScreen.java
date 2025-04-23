package com.github.FishMiner.domain.ports.in.ui.interfaces;

import com.github.FishMiner.ui.ports.out.domain.enums.ScreenType;

public interface IGameScreen {
    ScreenType getScreenType();

    void dispose();
}
