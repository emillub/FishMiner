package com.github.FishMiner.domain.ecs.entityFactories;

// Declares methods for creating each type of entity

import com.badlogic.ashley.core.Entity;

public interface IGameEntityFactory {
    Entity createFish();
    Entity createHook();
    Entity createSinker();
    // Add more methods if needed
}


