package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.MathUtils;
import com.github.FishMiner.domain.ecs.entityFactories.IGameEntityFactory;

public class SpawningSystem extends EntitySystem {

    private final IGameEntityFactory entityFactory;
    private float spawnTimer = 0f;
    private float spawnInterval = 3f; // Spawn every x seconds (adjust as needed)

    // If you want random intervals, you can define a range or separate logic
    public SpawningSystem(IGameEntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // Increment the timer
        spawnTimer += deltaTime;

        // Check if it's time to spawn
        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0f;

            // Create a fish using the factory
            // The factory already randomizes direction, speed, etc.
            getEngine().addEntity(entityFactory.createFish());

            // Optionally, you could spawn multiple fish or different entity types here
        }
    }

    // If you want to dynamically change spawnInterval, add a setter:
    public void setSpawnInterval(float interval) {
        this.spawnInterval = interval;
    }
}
