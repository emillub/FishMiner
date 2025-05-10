package com.github.FishMiner.ui.ports.out.domain.events.screenEvents;

import com.github.FishMiner.ui.ports.out.domain.enums.ScreenType;

public class PrepareScreenEvent extends AbstractScreenEvent {
    public PrepareScreenEvent(ScreenType screenType) {
        super(screenType);
    }
}
