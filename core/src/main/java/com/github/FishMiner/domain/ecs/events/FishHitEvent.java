package com.github.FishMiner.domain.ecs.events;

import com.badlogic.ashley.core.Entity;
import com.github.FishMiner.infrastructure.ports.in.IGameEvent;


public class FishHitEvent extends AbstractEntityEvent implements IGameEvent {
    public FishHitEvent(Entity fishableObject) {
        super(fishableObject);
    }
}

