package com.github.FishMiner.domain.states;

public interface IState {
    public IState getState(int i);
    void onEnter();
    void onExit();

    String getAnimationKey();
}
