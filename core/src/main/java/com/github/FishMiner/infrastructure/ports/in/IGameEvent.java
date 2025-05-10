package com.github.FishMiner.infrastructure.ports.in;


public interface IGameEvent {
    void setHandled();
    boolean isHandled();
}
