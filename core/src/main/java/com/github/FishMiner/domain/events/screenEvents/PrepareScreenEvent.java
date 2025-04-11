package com.github.FishMiner.domain.events.screenEvents;

import com.github.FishMiner.domain.ports.in.IGameEvent;
import com.github.FishMiner.ui.ports.out.ScreenType;

public class PrepareScreenEvent extends AbstractScreenEvent {
    public PrepareScreenEvent(ScreenType screenType) {
        super(screenType);
    }
}
