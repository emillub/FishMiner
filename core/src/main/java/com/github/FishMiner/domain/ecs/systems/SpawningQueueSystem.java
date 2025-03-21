package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.MathUtils;
import com.github.FishMiner.domain.ecs.components.SpawnQueueComponent;
import com.github.FishMiner.domain.ecs.entityFactories.FishTypes;
import com.github.FishMiner.domain.ecs.entityFactories.IGameEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.impl.BasicGameEntityFactory;
import com.github.FishMiner.domain.ecs.level.LevelConfig;

import java.util.HashMap;
import java.util.Map;

public class SpawningQueueSystem extends EntitySystem {
    private final IGameEntityFactory factory = new BasicGameEntityFactory();

    private float spawnTimer = 0f;
    private float spawnInterval = 6f; // Default value
    private Map<FishTypes, Float> spawnChances = new HashMap<>();

    public void configureFromLevel(LevelConfig config) {
        this.spawnInterval = config.getSpawnInterval();
        this.spawnChances = config.getSpawnChances();
    }

    @Override
    public void update(float deltaTime) {
        spawnTimer += deltaTime;

        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0f; // Reset the timer
            FishTypes type = pickRandomFishType();
            if (type != null) {
                Entity fish = factory.createFish(type, 1).get(0);
                getEngine().addEntity(fish); // Add the spawned fish to the engine
            }
        }
    }

    private FishTypes pickRandomFishType() {
        float roll = MathUtils.random();
        float cumulative = 0f;
        for (Map.Entry<FishTypes, Float> entry : spawnChances.entrySet()) {
            cumulative += entry.getValue();
            if (roll <= cumulative) return entry.getKey();
        }
        return null;
    }
}
