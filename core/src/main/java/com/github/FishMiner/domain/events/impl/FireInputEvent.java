package com.github.FishMiner.domain.events.impl;

import com.badlogic.ashley.core.Entity;
import com.github.FishMiner.domain.ecs.EntityEvent;
import com.github.FishMiner.domain.events.IGameEvent;


public class FireInputEvent extends EntityEvent implements IGameEvent {

    public FireInputEvent(Entity hook) {
        super(hook);
    }
}
