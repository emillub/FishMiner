package com.github.FishMiner.domain;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.components.InventoryComponent;
import com.github.FishMiner.domain.ecs.components.PlayerComponent;
import com.github.FishMiner.domain.ecs.entityFactories.IGameEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.oceanFactory.OceanEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.playerFactory.PlayerFactory;
import com.github.FishMiner.domain.ecs.systems.SpawningQueueSystem;
import com.github.FishMiner.domain.level.LevelConfig;
import com.github.FishMiner.domain.level.LevelConfigFactory;
import com.github.FishMiner.domain.states.WorldState;

import java.util.Random;

public class World {

    private final Engine engine;
    private final Configuration config;
    private final IGameEntityFactory factory;
    private final Random random = new Random();

    private WorldState state = WorldState.RUNNING;

    //private float score = 0f;
    private int targetScore = 0;
    private float timer = 60f;

    public World(Engine engine) {
        this.engine = engine;
        this.config = Configuration.getInstance();
        this.factory = new OceanEntityFactory(engine);
    }

    public void createLevel(LevelConfig config, float startingScore) {
        this.targetScore = config.getTargetScore(); // Set target score for current level

        SpawningQueueSystem spawningSystem = engine.getSystem(SpawningQueueSystem.class);
        if (spawningSystem != null) {
            spawningSystem.configureFromLevel(config);
            spawningSystem.setWorld(this);
        }
        timer = 60f;
        state = WorldState.RUNNING;
    }

    public void setState(WorldState newState) {
        this.state = newState;
    }

    public WorldState getState() {
        return state;
    }

    public float getScore() {
        ImmutableArray<Entity> players = engine.getEntitiesFor(Family.all(PlayerComponent.class).get());
        if (players.size() > 0) {
            InventoryComponent inventory = players.first().getComponent(InventoryComponent.class);
            if (inventory != null) {
                return inventory.money;
            }
        }
        return 0f;
    }

    public int getTargetScore() {
        return targetScore;
    }

    public void update(float deltaTime) {
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


    public Engine getEngine() {
        return this.engine;
    }




}
