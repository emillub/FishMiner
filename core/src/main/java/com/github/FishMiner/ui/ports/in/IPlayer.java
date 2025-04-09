package com.github.FishMiner.ui.ports.in;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.github.FishMiner.domain.ecs.components.PlayerComponent;

public interface IPlayer {
    public Entity getPlayerEntity();

    public Entity getHook();

    public Entity getSinker();

    public Entity getReel();
}
