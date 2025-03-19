package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.MathUtils;
import com.github.FishMiner.domain.ecs.components.SpawnQueueComponent;
import com.github.FishMiner.domain.ecs.entityFactories.FishTypes;
import com.github.FishMiner.domain.ecs.entityFactories.IGameEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.impl.BasicGameEntityFactory;
import com.github.FishMiner.domain.ecs.level.LevelConfig;
import com.github.FishMiner.domain.ecs.util.RandomInRangeUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * SpawningSystem is responsible for spawning fish entities at a set interval.
 */
public class SpawningQueueSystem extends EntitySystem {
    private final IGameEntityFactory factory = new BasicGameEntityFactory();

    private float spawnTimer = 0f;
    private float spawnInterval = 6f;
    private Map<FishTypes, Float> spawnChances = new HashMap<>();
    private int totalFishToSpawn = 0;
    private int spawnedCount = 0;

    public void configureFromLevel(LevelConfig config) {
        this.spawnInterval = config.getSpawnInterval();
        this.totalFishToSpawn = config.getTotalFishToSpawn();
        this.spawnedCount = 0;

        float total = 0f;
        for (float chance : config.getSpawnChances().values()) {
            total += chance;
        }

        this.spawnChances = new HashMap<>();
        for (Map.Entry<FishTypes, Float> entry : config.getSpawnChances().entrySet()) {
            this.spawnChances.put(entry.getKey(), entry.getValue() / total);
        }
    }


    @Override
    public void update(float deltaTime) {
        spawnTimer += deltaTime;

        if (spawnTimer >= spawnInterval && spawnedCount < totalFishToSpawn) {
            spawnTimer = 0f;
            FishTypes type = pickRandomFishType();
            if (type != null) {
                Entity fish = factory.createFish(type, 1).get(0);
                getEngine().addEntity(fish);
                spawnedCount++;
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

