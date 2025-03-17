package com.github.FishMiner.domain.events.impl;

import com.badlogic.ashley.core.Entity;
import com.github.FishMiner.domain.events.IGameEvent;

public class FishCapturedEvent implements IGameEvent {
    private Entity fish;

    public FishCapturedEvent(Entity fish) {
        this.fish = fish;
    }

    @Override
    public Entity getEventEntity() {
        return null;
    }
}
