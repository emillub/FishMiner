package com.github.FishMiner.domain.ecs.entityFactories.impl;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.components.*;
import com.github.FishMiner.domain.ecs.entityFactories.FishTypes;
import com.github.FishMiner.domain.ecs.entityFactories.IGameEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.playerFactory.HookFactory;

import java.util.LinkedList;


public class BasicGameEntityFactory implements IGameEntityFactory {

    private final Engine engine;
    private final FishFactory fishFactory;
    // HookFactory will be replaced with PlayerFactory soon
    private final HookFactory hookFactory;
    private final Configuration config = Configuration.getInstance();

    public BasicGameEntityFactory(Engine engine) {
        this.engine = engine;
        this.fishFactory = new FishFactory(engine);
        this.hookFactory = new HookFactory(engine);
    }

    @Override
    public LinkedList<Entity> createFish(FishTypes fishType, int amount) {
        LinkedList<Entity> fishList = new LinkedList<>();
        for (int i = 0; i < amount; i++) {
            fishList.add(fishFactory.createEntity(fishType));
        }
        return fishList;
    }
}
