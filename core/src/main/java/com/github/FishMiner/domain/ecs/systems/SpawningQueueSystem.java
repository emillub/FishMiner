package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.MathUtils;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.entityFactories.FishTypes;
import com.github.FishMiner.domain.ecs.entityFactories.IGameEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.oceanFactory.OceanEntityFactory;
import com.github.FishMiner.domain.level.LevelConfig;
import com.github.FishMiner.domain.World;
import com.github.FishMiner.domain.states.WorldState;

import java.util.HashMap;
import java.util.Map;

/**
 * SpawningSystem is responsible for spawning fish entities at a set interval.
 */
public class SpawningQueueSystem extends EntitySystem {

    private Engine engine;
    private float spawnTimer = 0f;
    private float spawnInterval;
    private Map<FishTypes, Float> spawnChances = new HashMap<>();
    private int totalFishToSpawn = 0;
    private int spawnedCount = 0;
    private IGameEntityFactory factory;
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
    public void addedToEngine(Engine engine) {
        this.engine = engine;
        this.factory = new OceanEntityFactory(this.engine);
    }

    @Override
    public void update(float deltaTime) {
        if (world != null && world.getState() != WorldState.RUNNING) {
            return;
        }

        if (!initialSpawnDone) {
            for (int i = 0; i < initialFishCount; i++) {
                FishTypes type = pickRandomFishType();
                if (type != null) {
                    Entity fish = factory.createFish(type, 1).get(0);
                    // Adjust the fish's x-position to be within the screen bounds
                    TransformComponent transformComponent = fish.getComponent(TransformComponent.class);
                    if (transformComponent != null) {
                        transformComponent.pos.x = MathUtils.random(Configuration.getInstance().getScreenWidth());
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
