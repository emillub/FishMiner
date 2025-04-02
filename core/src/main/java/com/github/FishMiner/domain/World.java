package com.github.FishMiner.domain;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.Logger;
import com.github.FishMiner.domain.ecs.entityFactories.IGameEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.oceanFactory.OceanEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.playerFactory.PlayerFactory;
import com.github.FishMiner.domain.ecs.systems.SpawningQueueSystem;
import com.github.FishMiner.domain.events.ScoreEvent;
import com.github.FishMiner.domain.level.LevelConfig;
import com.github.FishMiner.domain.listeners.IGameEventListener;
import com.github.FishMiner.domain.states.WorldState;

import java.util.Random;

public class World implements IGameEventListener<ScoreEvent> {
    private final static String TAG = "World";
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

    public void increaseScore(float scoreIncrease) {
        this.score += scoreIncrease;
    }

    private void decreaseScore(float scoreDecrease) {
        this.score = Math.max(score - scoreDecrease, 0);
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

    @Override
    public void onEvent(ScoreEvent event) {
        if (event.isHandled()) {
            Logger.getInstance().debug(TAG, "Received and denied a handled event");
            return;
        }
        float scoreDifference = event.getValue();
        if (scoreDifference < 0) {
            decreaseScore(scoreDifference);
        } else {
            increaseScore(scoreDifference);
        }
        event.setHandled(true);
    }

    @Override
    public Class<ScoreEvent> getEventType() {
        return null;
    }
}
