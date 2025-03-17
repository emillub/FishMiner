package com.github.FishMiner.domain.ecs.entityFactories;

import com.badlogic.ashley.core.Entity;

public interface IEntityFactory {
    Entity createEntity(int posX, int posY);
}
