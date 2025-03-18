package com.github.FishMiner.domain.events.impl;

import com.badlogic.ashley.core.Entity;
import com.github.FishMiner.domain.ecs.EntityEvent;
import com.github.FishMiner.domain.events.IGameEvent;


public class FishHitEvent extends EntityEvent implements IGameEvent {

    public FishHitEvent(Entity fishableObject) {
        super(fishableObject);
    }

}

