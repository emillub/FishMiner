package com.github.FishMiner.domain.ecs.entityFactories;

// Declares methods for creating each type of entity

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

import java.util.Vector;

public interface IGameEntityFactory {
    Entity createFish();
    Entity createHook();
    Entity createSinker();
    // Add more methods if needed
}


