package com.github.FishMiner.domain.states;

import com.badlogic.ashley.core.Entity;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.util.HookUtil;
import com.github.FishMiner.domain.events.GameEventBus;

public enum HookStates implements IState {
    SWINGING("swinging"),
    FIRE("fire"),
    REELING("reeling"),
    RETURNED("returned");

    private final String animationKey;

    HookStates(String animationKey) {
        this.animationKey = animationKey;
    }

    @Override
    public String getAnimationKey() {
        return animationKey;
    }
}
