package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.MathUtils;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;
import com.github.FishMiner.domain.ecs.entityFactories.FishTypes;
import com.github.FishMiner.domain.ecs.entityFactories.IGameEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.oceanFactory.OceanEntityFactory;
import com.github.FishMiner.domain.level.LevelConfig;
import com.github.FishMiner.domain.World;
import com.github.FishMiner.domain.states.WorldState;
import com.github.FishMiner.domain.ecs.entityFactories.GarbageTypes;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SpawningSystem is responsible for spawning fish entities at even intervals,
 * based on a finite pre-planned list of fish.
 */

public class SpawningQueueSystem extends EntitySystem {

    private PooledEngine engine;
    private IGameEntityFactory factory;
    private World world;

    private float spawnTimer = 0f;
    private float spawnInterval = 1f;

    private boolean initialSpawnDone = false;
    private int initialFishCount = 0;

    private int numGarbage = 0;

    private List<FishTypes> plannedFish = new ArrayList<>();
    private int spawnedCount = 0;

    private final float LEVEL_DURATION = 60f;

    public void configureFromLevel(LevelConfig config) {
        this.initialFishCount = config.getInitialFishCount();
        this.plannedFish = new ArrayList<>(config.getPlannedFish());
        Collections.shuffle(this.plannedFish); // if you want some randomness
        this.spawnedCount = 0;
        this.spawnTimer = 0f;
        this.spawnInterval = LEVEL_DURATION / Math.max(plannedFish.size(), 1); // avoid divide by zero
        this.initialSpawnDone = false;
        this.numGarbage = config.getNumGarbage();
    }

    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public void addedToEngine(Engine engine) {
        this.engine = (PooledEngine) engine;
        this.factory = new OceanEntityFactory(this.engine);
    }

    @Override
    public void update(float deltaTime) {
        if (world == null || world.getState() != WorldState.RUNNING) {
            return;
        }

        if (!initialSpawnDone) {
            spawnInitialFish();
            initialSpawnDone = true;
        }

        // Timed spawning of remaining fish
        spawnTimer += deltaTime;
        if (spawnedCount < plannedFish.size() && spawnTimer >= spawnInterval) {
            spawnTimer = 0f;
            spawnNextFish();
        }
    }

    private void spawnInitialFish() {
        float screenWidth = Configuration.getInstance().getScreenWidth();
        float margin = 20f;

        for (int i = 0; i < initialFishCount && i < plannedFish.size(); i++) {
            FishTypes type = plannedFish.get(i);
            Entity fish = factory.createFish(type, 1).get(0);

            TransformComponent transform = fish.getComponent(TransformComponent.class);
            if (transform != null) {
                float fishWidth = fish.getComponent(com.github.FishMiner.domain.ecs.components.FishComponent.class).width;
                float minX = margin;
                float maxX = screenWidth - fishWidth - margin;
                transform.pos.x = MathUtils.random(minX, maxX);
            }

            engine.addEntity(fish);
            spawnedCount++;
        }

        // Spawn garbage
        for (int i = 0; i < numGarbage; i++) {
            Entity garbage = ((OceanEntityFactory) factory).createGarbage(GarbageTypes.GARBAGE, 1).get(0);

            TransformComponent transform = garbage.getComponent(TransformComponent.class);
            VelocityComponent velocity = garbage.getComponent(VelocityComponent.class);
            if (transform != null && velocity != null) {
                float garbageWidth = garbage.getComponent(com.github.FishMiner.domain.ecs.components.FishComponent.class).width;
                float minX = margin;
                float maxX = screenWidth - garbageWidth - margin;
                transform.pos.x = MathUtils.random(minX, maxX);

                // Garbage should not move
                velocity.velocity.x = 0f;
            }

            engine.addEntity(garbage);
        }

        System.out.println("Spawned " + spawnedCount + " initial fish and " + numGarbage + " static garbage.");
    }

    private void spawnNextFish() {
        if (spawnedCount < plannedFish.size()) {
            spawnFish(plannedFish.get(spawnedCount));
            spawnedCount++;
        }
    }

    private void spawnFish(FishTypes type) {
        Entity fish = factory.createFish(type, 1).get(0);
        engine.addEntity(fish);
    }
}
