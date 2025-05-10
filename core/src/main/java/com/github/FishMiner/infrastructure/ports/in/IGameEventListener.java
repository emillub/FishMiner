package com.github.FishMiner.infrastructure.ports.in;

public interface IGameEventListener<E extends IGameEvent> {
    void onEvent(E event);
    Class<E> getEventType();
}

