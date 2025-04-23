package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.MathUtils;
import com.github.FishMiner.infrastructure.Configuration;
import com.github.FishMiner.infrastructure.Logger;
import com.github.FishMiner.domain.ecs.components.FishableComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;
import com.github.FishMiner.domain.ecs.entities.IEntityType;
import com.github.FishMiner.domain.ecs.entities.oceanFactory.OceanEntityFactory;
import com.github.FishMiner.domain.model.level.LevelConfig;
import com.github.FishMiner.domain.model.World;
import com.github.FishMiner.domain.ecs.states.WorldState;
import com.github.FishMiner.domain.ecs.entities.GarbageTypes;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SpawningSystem is responsible for spawning fish entities at even intervals,
 * based on a finite pre-planned list of fish.
 */

public class SpawningQueueSystem extends EntitySystem {
    private static final String TAG = "SpawningQueueSystem";
    private PooledEngine engine;
    private OceanEntityFactory factory;
    private World world;
    private float spawnTimer = 0f;
    private float spawnInterval = 1f;
    private boolean initialSpawnDone = false;
    private int initialFishCount = 0;
    private int numGarbage = 0;
    private List<IEntityType> plannedFish = new ArrayList<>();
    private int spawnedCount = 0;
    private final float LEVEL_DURATION = 60f;

    public void configureFromLevel(LevelConfig config) {
        clearPreviousEntities();

        this.initialFishCount = config.getInitialFishCount();
        this.plannedFish = config.getPlannedFish();

        Collections.shuffle(this.plannedFish); // if you want some randomness
        this.spawnedCount = 0;
        this.spawnTimer = 0f;
        this.spawnInterval = LEVEL_DURATION / Math.max(plannedFish.size(), 1); // avoid divide by zero
        this.initialSpawnDone = false;
        this.numGarbage = config.getNumGarbage();
        Logger.getInstance().log(TAG, "Configuring spawning system with " + config.getPlannedFish().size() + " planned fish");

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
            spawnNextFishableEntity();
        }
    }

    private void spawnInitialFish() {
        float screenWidth = Configuration.getInstance().getScreenWidth();
        float margin = 20f;

        for (int i = 0; i < initialFishCount && i < plannedFish.size(); i++) {
            IEntityType type = plannedFish.get(i);
            Entity fishableEntity = factory.createEntity(type);

            TransformComponent transform = fishableEntity.getComponent(TransformComponent.class);
            if (transform != null) {
                float fishWidth = fishableEntity.getComponent(FishableComponent.class).width;
                float minX = margin;
                float maxX = screenWidth - fishWidth - margin;
                transform.pos.x = MathUtils.random(minX, maxX);
            }


            engine.addEntity(fishableEntity);
            spawnedCount++;
        }

        // Spawn garbage
        for (int i = 0; i < numGarbage; i++) {
            Entity garbage = factory.createGarbage(GarbageTypes.GARBAGE, 1).get(0);

            TransformComponent transform = garbage.getComponent(TransformComponent.class);
            VelocityComponent velocity = garbage.getComponent(VelocityComponent.class);
            if (transform != null && velocity != null) {
                float garbageWidth = garbage.getComponent(FishableComponent.class).width;
                float minX;
                minX = margin;
                float maxX = screenWidth - garbageWidth - margin;
                transform.pos.x = MathUtils.random(minX, maxX);

                // Garbage should not move
                velocity.velocity.x = 0f;
            }

            engine.addEntity(garbage);
        }
        Logger.getInstance().log(TAG, "Spawned " + spawnedCount + " initial fish and " + numGarbage + " static garbage.");
    }

    private void spawnNextFishableEntity() {

        if (spawnedCount < plannedFish.size()) {
            spawnFishableEntity(plannedFish.get(spawnedCount));
            spawnedCount++;
        }
    }

    private void spawnFishableEntity(IEntityType type) {
        Entity fishableEntity = factory.createEntity(type);
        TransformComponent transform = fishableEntity.getComponent(TransformComponent.class);
        if (transform != null) {
            System.out.println("[DEBUG] Fish spawned: " + type + " at position " + transform.pos);
        }

        engine.addEntity(fishableEntity);
    }

    private void clearPreviousEntities() {
        List<Entity> toRemove = new ArrayList<>();

        for (Entity entity : engine.getEntities()) {
            if (entity.getComponent(FishableComponent.class) != null) {
                toRemove.add(entity);
            }
        }

        for (Entity entity : toRemove) {
            engine.removeEntity(entity);
        }

        Logger.getInstance().log(TAG, "Removed " + toRemove.size() + " fishable entities from previous level.");
    }

}
