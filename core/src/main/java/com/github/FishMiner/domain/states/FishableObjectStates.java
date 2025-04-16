package com.github.FishMiner.domain.states;

public enum FishableObjectStates implements IState {
    UNFISHABLE("UNFISHABLE"),
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
