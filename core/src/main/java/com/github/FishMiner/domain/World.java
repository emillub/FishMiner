package com.github.FishMiner.domain;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.entityFactories.IGameEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.oceanFactory.OceanEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.playerFactory.PlayerFactory;
import com.github.FishMiner.domain.ecs.systems.SpawningQueueSystem;
import com.github.FishMiner.domain.level.LevelConfig;
import com.github.FishMiner.domain.states.WorldState;

import java.util.Random;

public class World {

    private final PooledEngine engine;
    private final Configuration config;
    private final IGameEntityFactory factory;
    private final Random random = new Random();

    private WorldState state = WorldState.RUNNING;

    private float score = 0f;
    private int targetScore = 0;
    private float timer = 60f;

    public World(PooledEngine engine) {
        this.engine = engine;
        this.config = Configuration.getInstance();
        this.factory = new OceanEntityFactory(engine);
    }

    public void createLevel(LevelConfig config) {
        this.targetScore = config.getTargetScore(); // Set target score for current level

        SpawningQueueSystem spawningSystem = engine.getSystem(SpawningQueueSystem.class);
        if (spawningSystem != null) {
            spawningSystem.configureFromLevel(config);
            spawningSystem.setWorld(this);
        }

        score = 0f;
        timer = 60f;
        state = WorldState.RUNNING;
    }

    public void setState(WorldState newState) {
        this.state = newState;
    }

    public WorldState getState() {
        return state;
    }

    public void increaseScore(float delta) {
        this.score += delta;
    }

    public float getScore() {
        return score;
    }

    public int getTargetScore() {
        return targetScore;
    }

    public void update(float deltaTime) {
        if (state == WorldState.RUNNING) {
            timer -= deltaTime;
            if (timer <= 0) {
                timer = 0;
                if (score >= targetScore) {
                    state = WorldState.WON;
                    System.out.println("Game State changed to: WON");
                } else {
                    state = WorldState.LOST;
                    System.out.println("Game State changed to: LOST");
                }
            }
        }
    }
    public float getTimer() {
        return timer;
    }

}
