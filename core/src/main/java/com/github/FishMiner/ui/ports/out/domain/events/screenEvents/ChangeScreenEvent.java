package com.github.FishMiner.ui.ports.out.domain.events.screenEvents;

import com.github.FishMiner.ui.ports.out.domain.enums.ScreenType;

public class ChangeScreenEvent extends AbstractScreenEvent {
    protected boolean handled;
    public ChangeScreenEvent(ScreenType screenType) {
        super(screenType);
    }
}
