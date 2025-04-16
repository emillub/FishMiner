package com.github.FishMiner.domain.states;

public enum FishingRodState implements IState {
    SWINGING("swinging"),
    FIRE("fire"),
    REELING("reeling"),
    RETURNED("returned");

    private final String animationKey;

    FishingRodState(String animationKey) {
        this.animationKey = animationKey;
    }

    @Override
    public String getAnimationKey() {
        return animationKey;
    }
}
