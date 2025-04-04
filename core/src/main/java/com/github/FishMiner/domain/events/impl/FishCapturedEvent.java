package com.github.FishMiner.domain.events.impl;

import com.badlogic.ashley.core.Entity;
import com.github.FishMiner.domain.events.EntityEvent;
import com.github.FishMiner.domain.events.IGameEvent;

public class FishCapturedEvent extends EntityEvent implements IGameEvent {
    private final int value;
    public FishCapturedEvent(Entity fish, int value) {
        super(fish); this.value = value;
    }

    public int getValue() {
        return value;
    }

}
