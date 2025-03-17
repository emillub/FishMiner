package com.github.FishMiner.domain.ecs.entityFactories.impl;

import com.badlogic.ashley.core.Entity;
import com.github.FishMiner.domain.ecs.components.SpawnQueueComponent;

import java.util.List;

/**
 * Factory class for creating level entities that contain fish spawn queues.
 */
public class LevelFactory {

    /**
     * Creates a level entity containing a spawn queue of fish entities.
     *
     * @param fishForLevel The list of fish entities to be spawned.
     * @param spawnInterval The time interval between spawns.
     * @return An entity with a SpawnQueueComponent.
     */
    public static Entity createEntity(List<Entity> fishForLevel, float spawnInterval) {
        Entity levelEntity = new Entity();
        SpawnQueueComponent spawnQueue = new SpawnQueueComponent();

        for (Entity fish : fishForLevel) {
            spawnQueue.addFish(fish);
        }

        spawnQueue.setSpawnInterval(spawnInterval);
        levelEntity.add(spawnQueue);

        return levelEntity;
    }

}
