package com.github.FishMiner.domain.events.ecsEvents;

import com.badlogic.ashley.core.Entity;
import com.github.FishMiner.domain.ports.in.IGameEvent;


public class FishHitEvent extends AbstractEntityEvent implements IGameEvent {
    public FishHitEvent(Entity fishableObject) {
        super(fishableObject);
    }
}

