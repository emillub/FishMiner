package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.ComponentMapper;
import com.github.FishMiner.domain.ecs.components.SpawnQueueComponent;
import com.github.FishMiner.domain.ecs.util.RandomInRangeUtil;

/**
 * SpawningSystem is responsible for spawning fish entities at a set interval.
 */
public class SpawningQueueSystem extends IteratingSystem {
    private final ComponentMapper<SpawnQueueComponent> spawnQueueMapper = ComponentMapper.getFor(SpawnQueueComponent.class);

    private float spawnTimer = 0f;
    private float spawnInterval = 6f; // Time interval between spawns
    private float currentSpawnInterval;

    /**
     * Initializes the spawning system.
     */
    public SpawningQueueSystem() {
        super(Family.all(SpawnQueueComponent.class).get()); // ✅ Processes only entities with SpawnQueueComponent
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // Increment the timer
        spawnTimer += deltaTime;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        SpawnQueueComponent spawnQueue = spawnQueueMapper.get(entity);
        currentSpawnInterval = RandomInRangeUtil.getRandomFloatInRange(0, spawnInterval);

        if (spawnTimer >= currentSpawnInterval && !spawnQueue.isEmpty()) {
            spawnTimer = 0f;

            // Spawn the next fish entity
            Entity fish = spawnQueue.pollFish();
            if (fish != null) {
                getEngine().addEntity(fish);
            }
        }
    }

    /**
     * Updates the spawn interval dynamically.
     *
     * @param interval The new spawn interval in seconds.
     */
    public void setSpawnInterval(float interval) {
        this.spawnInterval = interval;
    }
}
