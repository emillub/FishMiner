package com.github.FishMiner.domain.ecs;

import com.badlogic.ashley.core.Entity;
import com.github.FishMiner.domain.events.IGameEvent;
import com.github.FishMiner.domain.events.IGameEventListener;
import com.github.FishMiner.domain.events.impl.FishHitEvent;

public class EntityStateManager implements IGameEventListener {
    @Override
    public void onEvent(IGameEvent event) {
        if (event instanceof FishHitEvent) {
            Entity fish = event.getEventEntity();
        }
    }
}
