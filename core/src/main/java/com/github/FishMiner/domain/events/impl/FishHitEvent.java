package com.github.FishMiner.domain.events.impl;

import com.badlogic.ashley.core.Entity;
import com.github.FishMiner.domain.events.IGameEvent;


public class FishHitEvent implements IGameEvent {

    private final Entity fish;

    public FishHitEvent(Entity fish) {
        this.fish = fish;
    }

    @Override
    public Entity getEventEntity() {
        return fish;
    }
}

