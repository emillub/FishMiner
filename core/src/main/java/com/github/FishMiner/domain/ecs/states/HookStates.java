package com.github.FishMiner.domain.ecs.states;

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
