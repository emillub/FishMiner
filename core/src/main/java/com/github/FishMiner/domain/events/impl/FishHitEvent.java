package com.github.FishMiner.domain.events.impl;

import com.badlogic.ashley.core.Entity;
import com.github.FishMiner.domain.events.AbstractEntityEvent;
import com.github.FishMiner.domain.ports.in.IGameEvent;


public class FishHitEvent extends AbstractEntityEvent implements IGameEvent {
    public FishHitEvent(Entity fishableObject) {
        super(fishableObject);
    }
}

