package com.github.FishMiner.domain.events.ecsEvents;

import com.badlogic.ashley.core.Entity;
import com.github.FishMiner.common.Logger;
import com.github.FishMiner.domain.ecs.components.FishableComponent;
import com.github.FishMiner.common.ValidateUtil;
import com.github.FishMiner.domain.ports.in.IGameEvent;

public class FishCapturedEvent extends AbstractEntityEvent implements IGameEvent {
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
        FishableComponent fishComponent = getTarget().getComponent(FishableComponent.class);
        // TODO: remove string
        ValidateUtil.validateNotNull(fishComponent, "remove this later");
        return fishComponent.getValue();
    }
}
