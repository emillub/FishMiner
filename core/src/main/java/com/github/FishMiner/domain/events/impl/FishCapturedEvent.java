package com.github.FishMiner.domain.events.impl;

import com.badlogic.ashley.core.Entity;
import com.github.FishMiner.domain.events.EntityEvent;
import com.github.FishMiner.domain.events.IGameEvent;

public class FishCapturedEvent extends EntityEvent implements IGameEvent {
    public FishCapturedEvent(Entity fish) {
        super(fish);
    }

}
