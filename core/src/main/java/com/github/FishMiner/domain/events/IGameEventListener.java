package com.github.FishMiner.domain.events;

public interface IGameEventListener<E extends IGameEvent> {
    void onEvent(E event);
}
