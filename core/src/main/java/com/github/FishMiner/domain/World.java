package com.github.FishMiner.domain;

import com.badlogic.ashley.core.PooledEngine;
import com.github.FishMiner.common.Logger;
import com.github.FishMiner.domain.ecs.systems.AnimationSystem;
import com.github.FishMiner.domain.ecs.systems.AttachmentSystem;
import com.github.FishMiner.domain.ecs.systems.BackgroundRenderSystem;
import com.github.FishMiner.domain.ecs.systems.CollisionSystem;
import com.github.FishMiner.domain.ecs.systems.FishingRodSystem;
import com.github.FishMiner.domain.ecs.systems.FishingSystem;
import com.github.FishMiner.domain.ecs.systems.HookInputSystem;
import com.github.FishMiner.domain.ecs.systems.HookSystem;
import com.github.FishMiner.domain.ecs.systems.MovementSystem;
import com.github.FishMiner.domain.ecs.systems.PhysicalSystem;
import com.github.FishMiner.domain.ecs.systems.RenderingSystem;
import com.github.FishMiner.domain.ecs.systems.RotationSystem;
import com.github.FishMiner.domain.ecs.systems.UpgradeSystem;
import com.github.FishMiner.domain.factories.oceanFactory.OceanEntityFactory;
import com.github.FishMiner.domain.ecs.systems.SpawningQueueSystem;
import com.github.FishMiner.domain.events.ScoreEvent;
import com.github.FishMiner.domain.level.LevelConfig;
import com.github.FishMiner.domain.level.LevelConfigFactory;
import com.github.FishMiner.domain.managers.ScreenManager;
import com.github.FishMiner.domain.ports.in.IGameEventListener;
import com.github.FishMiner.domain.states.WorldState;
import com.github.FishMiner.ui.ports.out.ScreenType;

public class World implements IGameEventListener<ScoreEvent> {
    private final static String TAG = "World";
    private final PooledEngine engine;
    private final OceanEntityFactory factory;
    private WorldState state = WorldState.RUNNING;
    private ScoreEvent removeTargetScoreEvent = null;
    private int score;
    private int levelNumber = 1;
    private int targetScore = 0;
    private float timer = 60f;
    private boolean finalScorePosted = false;
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
    }

    public void setState(WorldState newState) {
        this.state = newState;
    }

    public WorldState getState() {
        return state;
    }
    public int getTargetScore() {
        return targetScore;
    }

    protected void update(float deltaTime) {
        if (this.isPaused()) {
            return;
        }

        if (state == WorldState.RUNNING) {
            timer -= deltaTime;
            if (timer <= 0 && !finalScorePosted) {
                timer = 0;
                // Determine win or loss based on the result of the ScoreEvent and current score.
                if (targetScore <= score) {
                    Logger.getInstance().log(TAG, "Level completed: " + levelNumber);
                    state = WorldState.WON;
                    GameEventBus.getInstance().post(new ScoreEvent(-targetScore));
                    state = WorldState.PAUSED;
                    ScreenManager.getInstance().switchScreenTo(ScreenType.LEVEL_COMPLETE);
                } else {
                    Logger.getInstance().log(TAG, "Game over. Levels completed: " + levelNumber + ". Score: " + score);
                    state = WorldState.LOST;
                    ScreenManager.getInstance().switchScreenTo(ScreenType.LEVEL_LOST);
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

    public boolean isLost() {
        return state == WorldState.LOST;
    }

    public boolean isWon() {
        return state == WorldState.WON;
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

    public float getScore() {
        return (float) score;
    }

    @Override
    public void onEvent(ScoreEvent event) {
        if (event.isHandled()) {
            Logger.getInstance().debug(TAG, "Received and denied a handled event");
            return;
        }
        int newScore = (int) event.getScore();
        if (score > newScore) {
            score = newScore;
            // TODO: display green text with score increase in PlayScreen
        } else if (score < newScore) {
            score = newScore;
            // TODO: display red text with score decrease in PlayScreen
        }
        event.setHandled();
    }

    @Override
    public Class<ScoreEvent> getEventType() {
        return ScoreEvent.class;
    }
}
