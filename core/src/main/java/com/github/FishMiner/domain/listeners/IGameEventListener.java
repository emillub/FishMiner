package com.github.FishMiner.domain.listeners;

import com.github.FishMiner.domain.events.IGameEvent;

public interface IGameEventListener<E extends IGameEvent> {
    void onEvent(E event);
    Class<E> getEventType();
}

