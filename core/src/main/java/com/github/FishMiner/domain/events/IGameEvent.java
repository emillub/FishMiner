package com.github.FishMiner.domain.events;


import com.badlogic.ashley.core.Entity;

public interface IGameEvent {
    Entity getEventEntity();
}
