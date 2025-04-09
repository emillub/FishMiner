package com.github.FishMiner.domain.states;

public enum TraderStates implements IState {
    NORMAL("normal"),
    BUY("buy");

    private final String animationKey;

    TraderStates(String animationKey) {
        this.animationKey = animationKey;
    }

    @Override
    public String getAnimationKey() {
        return animationKey;
    }
}
