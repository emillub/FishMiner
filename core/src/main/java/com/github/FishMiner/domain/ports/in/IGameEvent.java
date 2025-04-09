package com.github.FishMiner.domain.ports.in;


import com.badlogic.ashley.core.Entity;

public interface IGameEvent {
    void setHandled();
    boolean isHandled();
}
