package com.github.FishMiner.domain;

import com.badlogic.ashley.core.PooledEngine;
import com.github.FishMiner.common.Logger;
import com.github.FishMiner.domain.factories.oceanFactory.OceanEntityFactory;
import com.github.FishMiner.domain.ecs.systems.SpawningQueueSystem;
import com.github.FishMiner.domain.events.ScoreEvent;
import com.github.FishMiner.domain.level.LevelConfig;
import com.github.FishMiner.domain.level.LevelConfigFactory;
import com.github.FishMiner.domain.ports.in.IGameEventListener;
import com.github.FishMiner.domain.states.WorldState;

public class World implements IGameEventListener<ScoreEvent> {
    private final static String TAG = "World";
    private final PooledEngine engine;
    private final OceanEntityFactory factory;
    private WorldState state = WorldState.RUNNING;
    private int score;
    private int levelNumber = 1;
    private int targetScore = 0;
    private float timer = 60f;
    private boolean finalScorePosted = false;
    private boolean levelCompleted = false;

    public World(PooledEngine engine) {
        this.engine = engine;
        this.factory = new OceanEntityFactory(engine);
        this.score = 0;
    }

    public void createLevel(LevelConfig config, int startingScore) {
        this.createLevel(config, startingScore, 60f);

    }

    protected void nextLevel(int playerScore) {
        levelNumber++;
        LevelConfig levelConfig = LevelConfigFactory.generateLevel(levelNumber, playerScore);
        createLevel(levelConfig, playerScore);
    }

    public void createLevel(LevelConfig config, int startingScore, Float customTimer) {
        this.targetScore = config.getTargetScore();

        SpawningQueueSystem spawningSystem = engine.getSystem(SpawningQueueSystem.class);

        if (spawningSystem != null) {
            spawningSystem.configureFromLevel(config);
            spawningSystem.setWorld(this);
        }

        timer = (customTimer != null) ? customTimer : 60f;
        score = startingScore;
        state = WorldState.RUNNING;

        finalScorePosted = false;
        levelCompleted = false;

    }

    public void setState(WorldState newState) {
        this.state = newState;
    }

    public WorldState getState() {
        return state;
    }


    public float getScore() {
        return score;
    }

    public int getTargetScore() {
        return targetScore;
    }

    protected void update(float deltaTime) {
        if (state == WorldState.PAUSED) {
            return;
        }

        if (state == WorldState.RUNNING) {
            timer -= deltaTime;

            if (timer <= 0 && !finalScorePosted) {
                timer = 0f;
                finalScorePosted = true;

                if (score >= targetScore) {
                    state = WorldState.WON;
                    Logger.getInstance().log(TAG, "Level completed: " + levelNumber);
                } else {
                    state = WorldState.LOST;
                    Logger.getInstance().log(TAG, "Game over. Levels completed: " + levelNumber);
                }
            }
        }
    }


    public float getTimer() {
        return timer;
    }

    public boolean isPaused() {
        return state == WorldState.PAUSED;
    }

    public void togglePause() {
        if (state == WorldState.PAUSED) {
            state = WorldState.RUNNING;
        } else if (state == WorldState.RUNNING) {
            state = WorldState.PAUSED;
        }
    }

    public int getLevel() {
        return levelNumber;
    }

    @Override
    public void onEvent(ScoreEvent event) {
        if (event.isHandled()) {
            Logger.getInstance().debug(TAG, "Received and denied a handled event");
            return;
        }

        int newScore = (int) event.getScore();
        if (score != newScore) {
            score = newScore;
        }

        event.setHandled();
    }



    @Override
    public Class<ScoreEvent> getEventType() {
        return ScoreEvent.class;
    }
}
