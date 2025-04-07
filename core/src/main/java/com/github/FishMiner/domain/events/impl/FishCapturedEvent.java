package com.github.FishMiner.domain.events.impl;

import com.badlogic.ashley.core.Entity;
import com.github.FishMiner.Logger;
import com.github.FishMiner.domain.ecs.components.FishComponent;
import com.github.FishMiner.domain.ecs.util.ValidateUtil;
import com.github.FishMiner.domain.events.EntityEvent;
import com.github.FishMiner.domain.events.IGameEvent;

public class FishCapturedEvent extends EntityEvent implements IGameEvent {
    private static final String TAG = "FishCapturedEvent";
    public FishCapturedEvent(Entity fish, Entity player) {
        super(fish, player);
        Logger.getInstance().log(TAG, player + " captured fish: " + fish);
    }

    @Override
    public void setHandled() {
        super.setHandled();
        Logger.getInstance().log(TAG, "Event is set as handled");
    }

    public float getValue() {
        FishComponent fishComponent = getTarget().getComponent(FishComponent.class);
        // TODO: remove string
        ValidateUtil.validateNotNull(fishComponent, "remove this later");
        return fishComponent.getValue();
    }
}
