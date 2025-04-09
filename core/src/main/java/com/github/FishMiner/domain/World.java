package com.github.FishMiner.domain;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.PooledEngine;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.Logger;
import com.github.FishMiner.domain.ecs.entityFactories.IOceanEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.oceanFactory.OceanEntityFactory;
import com.github.FishMiner.domain.ecs.systems.SpawningQueueSystem;
import com.github.FishMiner.domain.events.impl.ScoreEvent;
import com.github.FishMiner.domain.level.LevelConfig;
import com.github.FishMiner.domain.ports.in.IGameEventListener;
import com.github.FishMiner.domain.states.WorldState;

import java.util.Random;

public class World implements IGameEventListener<ScoreEvent> {
    private final static String TAG = "World";
    private final PooledEngine engine;
    private final Configuration config;
    private final IOceanEntityFactory factory;
    private final Random random = new Random();
    private WorldState state = WorldState.RUNNING;
    private int score;
    private int targetScore = 0;
    private float timer = 60f;

    public World(PooledEngine engine) {
        this.engine = engine;
        this.config = Configuration.getInstance();
        this.factory = new OceanEntityFactory(engine);
        this.score = 0;
    }

    public void createLevel(LevelConfig config, int startingScore, Float customTimer) {
        this.targetScore = config.getTargetScore(); // Set target score for current level

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


    public float getScore() {
        return score;
    }

    //public float getScore() {
    //    ImmutableArray<Entity> players = engine.getEntitiesFor(Family.all(PlayerComponent.class).get());
    //    if (players.size() > 0) {
    //        InventoryComponent inventory = players.first().getComponent(InventoryComponent.class);
    //        if (inventory != null) {
    //            return inventory.money;
    //        }
    //    }
    //    return 0f;
    //}

    public int getTargetScore() {
        return targetScore;
    }

    public void update(float deltaTime) {
        if (state == WorldState.PAUSED){return;}

        if (state == WorldState.RUNNING) {
            timer -= deltaTime;
            if (timer <= 0) {
                timer = 0;
                if (getScore() >= targetScore) {
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

    public Engine getEngine() {
        return this.engine;
    }
}
