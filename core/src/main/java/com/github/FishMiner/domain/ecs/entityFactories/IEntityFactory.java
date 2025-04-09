package com.github.FishMiner.domain.ecs.entityFactories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;

public interface IEntityFactory {
    Entity createEntity(PooledEngine engine, int posX, int posY);
}
