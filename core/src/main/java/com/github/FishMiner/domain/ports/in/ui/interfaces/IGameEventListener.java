package com.github.FishMiner.domain.ports.in.ui.interfaces;

public interface IGameEventListener<E extends IGameEvent> {
    void onEvent(E event);
    Class<E> getEventType();
}

