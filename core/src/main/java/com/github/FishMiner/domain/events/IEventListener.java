package com.github.FishMiner.domain.events;

public interface IEventListener<E extends IGameEvent> {
    void onEvent(E event);
}
