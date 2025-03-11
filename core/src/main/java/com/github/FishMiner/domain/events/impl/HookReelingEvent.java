package com.github.FishMiner.domain.events.impl;

import com.badlogic.ashley.core.Entity;
import com.github.FishMiner.domain.events.IGameEvent;

public class HookReelingEvent implements IGameEvent {
    private final Entity hook;

    public HookReelingEvent(Entity hook) {
        this.hook = hook;
    }

    @Override
    public Entity getEventEntity() {
        return hook;
    }
}
