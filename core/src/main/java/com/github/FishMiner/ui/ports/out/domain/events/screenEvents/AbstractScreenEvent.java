package com.github.FishMiner.ui.ports.out.domain.events.screenEvents;

import com.github.FishMiner.infrastructure.ports.in.IGameEvent;
import com.github.FishMiner.ui.ports.out.domain.enums.ScreenType;

public abstract class AbstractScreenEvent implements IGameEvent {
    protected boolean handled;
    protected final ScreenType screenType;

    protected AbstractScreenEvent(ScreenType screenType) {
        this.screenType = screenType;
    }

    public ScreenType getScreenType() {
        return screenType;
    }

    public void setHandled() {
        this.handled = true;
    }

    public boolean isHandled() {
        return handled;
    }
}

