package com.github.FishMiner.domain.events.impl;

import com.badlogic.ashley.core.Entity;
import com.github.FishMiner.Logger;
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

}
