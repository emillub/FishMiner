package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.MathUtils;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.components.PositionComponent;
import com.github.FishMiner.domain.ecs.entityFactories.FishTypes;
import com.github.FishMiner.domain.ecs.entityFactories.IGameEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.impl.BasicGameEntityFactory;
import com.github.FishMiner.domain.level.LevelConfig;
import com.github.FishMiner.domain.World;

import java.util.HashMap;
import java.util.Map;

public class SpawningQueueSystem extends EntitySystem {
    private final IGameEntityFactory factory = new BasicGameEntityFactory();

    private float spawnTimer = 0f;
    private float spawnInterval;
    private Map<FishTypes, Float> spawnChances = new HashMap<>();
    private World world;

    private boolean initialSpawnDone = false;
    private int initialFishCount = 0;

    public void configureFromLevel(LevelConfig config) {
        this.spawnInterval = config.getSpawnInterval();
        this.spawnChances = config.getSpawnChances();
        this.initialFishCount = config.getInitialFishCount();
        initialSpawnDone = false;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public void update(float deltaTime) {
        if (world != null && world.getState() != World.WORLD_STATE_RUNNING) {
            return;
        }

        if (!initialSpawnDone) {
            for (int i = 0; i < initialFishCount; i++) {
                FishTypes type = pickRandomFishType();
                if (type != null) {
                    Entity fish = factory.createFish(type, 1).get(0);
                    // Adjust the fish's x-position to be within the screen bounds
                    PositionComponent pos = fish.getComponent(PositionComponent.class);
                    if (pos != null) {
                        pos.position.x = MathUtils.random(Configuration.getInstance().getScreenWidth());
                    }
                    getEngine().addEntity(fish);
                }
            }
            initialSpawnDone = true;
        }

        spawnTimer += deltaTime;

        if (spawnTimer >= spawnInterval * 3) {
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
