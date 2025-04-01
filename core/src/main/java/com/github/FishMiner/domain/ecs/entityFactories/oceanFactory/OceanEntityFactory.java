package com.github.FishMiner.domain.ecs.entityFactories.oceanFactory;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.entityFactories.FishTypes;
import com.github.FishMiner.domain.ecs.entityFactories.IGameEntityFactory;

import java.util.LinkedList;


public class OceanEntityFactory implements IGameEntityFactory {

    private final PooledEngine engine;
    private final FishFactory fishFactory;
    // HookFactory will be replaced with PlayerFactory soon
    private final Configuration config = Configuration.getInstance();

    public OceanEntityFactory(PooledEngine engine) {
        this.engine = engine;
        this.fishFactory = new FishFactory(engine);
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
