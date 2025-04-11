package com.github.FishMiner.domain.events.screenEvents;

import com.github.FishMiner.domain.ports.in.IGameEvent;
import com.github.FishMiner.ui.ports.out.ScreenType;

public class ChangeScreenEvent extends AbstractScreenEvent {
    protected boolean handled;
    public ChangeScreenEvent(ScreenType screenType) {
        super(screenType);
    }
}
