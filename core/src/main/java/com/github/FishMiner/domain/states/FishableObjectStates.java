package com.github.FishMiner.domain.states;

import com.github.FishMiner.Logger;

public enum FishableObjectStates implements IState {
    FISHABLE("FISHABLE"),
    HOOKED("HOOKED"),
    REELING("REELING"),
    CAPTURED("CAPTURED"),

    // Shark stuff:
    ATTACKING("ATTACKING"),
    LEAVING("LEAVING");

    private final String animationKey;

    FishableObjectStates(String animationKey) {
        this.animationKey = animationKey;
    }

    @Override
    public String getAnimationKey() {
        return animationKey;
    }
}
