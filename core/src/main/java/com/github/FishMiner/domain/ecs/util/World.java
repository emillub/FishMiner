package com.github.FishMiner.domain.ecs.util;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.entityFactories.FishTypes;
import com.github.FishMiner.domain.ecs.entityFactories.IGameEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.impl.BasicGameEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.impl.LevelFactory;
import com.github.FishMiner.domain.ecs.level.LevelConfig;
import com.github.FishMiner.domain.ecs.systems.SpawningQueueSystem;

import java.util.LinkedList;
import java.util.Random;

public class World {

    public static final int WORLD_STATE_RUNNING = 0;
    public static final int WORLD_STATE_NEXT_LEVEL = 1;
    public static final int WORLD_STATE_GAME_OVER = 2;

    private final PooledEngine engine;
    private final Configuration config;
    private final IGameEntityFactory factory;
    private final Random random = new Random();

    private int state = WORLD_STATE_RUNNING;
    private float score = 0f;

    public World(PooledEngine engine) {
        this.engine = engine;
        this.config = Configuration.getInstance();
        this.factory = new BasicGameEntityFactory();
    }

    public void createLevel(LevelConfig config) {
        // Add hook and background (static setup)
        createHook();
        createBackground();

        // (Optional) Add a static "level entity" to track score/goal if LevelFactory still used
        Entity levelEntity = LevelFactory.createEntity(new LinkedList<>(), config.getTargetScore());
        engine.addEntity(levelEntity);

        // Configure spawn system (still preferred here so World controls the entire level logic)
        SpawningQueueSystem spawningSystem = engine.getSystem(SpawningQueueSystem.class);
        if (spawningSystem != null) {
            spawningSystem.configureFromLevel(config);
        }

        // Reset world state
        score = 0f;
        state = WORLD_STATE_RUNNING;
    }

    private void createHook() {
        Entity hook = factory.createHook();
        engine.addEntity(hook);
    }

    private void createBackground() {
        // Optional: Move this to a separate BackgroundFactory class later
        Entity background = new Entity();
        // Add TransformComponent, TextureComponent etc. as needed
        engine.addEntity(background);
    }

    public void setState(int newState) {
        this.state = newState;
    }

    public int getState() {
        return state;
    }

    public void increaseScore(float delta) {
        this.score += delta;
    }

    public float getScore() {
        return score;
    }
}
