package com.github.FishMiner.domain.states;

public enum FishableObjectStates implements IState {
    FISHABLE("fishable"),
    HOOKED("hooked"),
    REELING("reeling"),
    CAPTURED("captured"),

    // Shark stuff:
    ATTACKING("attacking"),
    LEAVING("leaving");

    private final String animationKey;

    FishableObjectStates(String animationKey) {
        this.animationKey = animationKey;
    }

    @Override
    public String getAnimationKey() {
        return animationKey;
    }
}
