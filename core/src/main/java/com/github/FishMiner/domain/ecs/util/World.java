package com.github.FishMiner.domain.ecs.util;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.entityFactories.FishTypes;
import com.github.FishMiner.domain.ecs.entityFactories.IGameEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.impl.BasicGameEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.impl.LevelFactory;
import com.github.FishMiner.domain.ecs.systems.SpawningQueueSystem;
import com.github.FishMiner.domain.ecs.level.LevelConfig;

import java.util.LinkedList;
import java.util.Random;

public class World {

    public static final int WORLD_STATE_RUNNING = 0;
    public static final int WORLD_STATE_WIN = 1;
    public static final int WORLD_STATE_LOSE = 2;

    private final PooledEngine engine;
    private final Configuration config;
    private final IGameEntityFactory factory;
    private final Random random = new Random();

    private int state = WORLD_STATE_RUNNING;
    private float score = 0f;
    private int targetScore = 0;
    private float timer = 60f; // Start the timer at 60 seconds


    public World(PooledEngine engine) {
        this.engine = engine;
        this.config = Configuration.getInstance();
        this.factory = new BasicGameEntityFactory();
    }

    // Method to create a new level
    public void createLevel(LevelConfig config) {
         createHook();
        createBackground();

        // Add level setup (Target score and spawn system)
        this.targetScore = config.getTargetScore(); // Set target score for current level

        // Configure spawn system to use the level config
        SpawningQueueSystem spawningSystem = engine.getSystem(SpawningQueueSystem.class);
        if (spawningSystem != null) {
            spawningSystem.configureFromLevel(config); // Setup spawn interval and spawn chances
        }

        // Reset the score at the start of the level
        score = 0f;
        timer = 60f;
        state = WORLD_STATE_RUNNING;
    }

    // Method to create hook entity
    private void createHook() {
        Entity hook = factory.createHook();
        engine.addEntity(hook);
    }

    // Method to create background entity
    private void createBackground() {
        Entity background = new Entity();
        engine.addEntity(background);
    }

    // Method to set the current world state
    public void setState(int newState) {
        this.state = newState;
    }

    // Method to get the current world state
    public int getState() {
        return state;
    }

    // Method to increase the score based on delta time (for fish caught)
    public void increaseScore(float delta) {
        this.score += delta;
    }

    // Method to get the current score
    public float getScore() {
        return score;
    }

    // Method to get the target score for the current level
    public int getTargetScore() {
        return targetScore;
    }

    // Method to update the world time and check if the level is completed
    public void update(float deltaTime) {
        if (timer > 0) {
            timer -= deltaTime; // Decrease time by delta time each frame
        }
    }

    public float getTimer() {
        return timer;
    }
}
