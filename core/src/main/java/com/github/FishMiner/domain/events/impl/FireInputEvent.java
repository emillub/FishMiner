package com.github.FishMiner.domain.events.impl;

import com.badlogic.ashley.core.Entity;
import com.github.FishMiner.domain.events.IGameEvent;


public class FireInputEvent implements IGameEvent {

    private final Entity hook;

    public FireInputEvent(Entity hook) {
        this.hook = hook;
    }


    @Override
    public Entity getEventEntity() {
        return getHook();
    }

    private Entity getHook() {
        return hook;
    }
}
