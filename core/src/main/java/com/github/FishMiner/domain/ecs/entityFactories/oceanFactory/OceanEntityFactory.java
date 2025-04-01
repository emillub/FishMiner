package com.github.FishMiner.domain.ecs.entityFactories.oceanFactory;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.entityFactories.FishTypes;
import com.github.FishMiner.domain.ecs.entityFactories.GarbageTypes;
import com.github.FishMiner.domain.ecs.entityFactories.IGameEntityFactory;

import java.util.LinkedList;


public class OceanEntityFactory implements IGameEntityFactory {

    private final PooledEngine engine;
    private final FishFactory fishFactory;
    private  final GarbageFactory garbageFactory;
    // HookFactory will be replaced with PlayerFactory soon
    private final Configuration config = Configuration.getInstance();

    public OceanEntityFactory(PooledEngine engine) {
        this.engine = engine;
        this.fishFactory = new FishFactory(engine);
        this.garbageFactory = new GarbageFactory(engine);

    }

    @Override
    public LinkedList<Entity> createFish(FishTypes fishType, int amount) {
        LinkedList<Entity> fishList = new LinkedList<>();
        for (int i = 0; i < amount; i++) {
            fishList.add(fishFactory.createEntity(fishType));
        }
        return fishList;
    }

    public LinkedList<Entity> createGarbage(GarbageTypes garbageType, int amount) {
        LinkedList<Entity> garbageList = new LinkedList<>();
        for (int i = 0; i < amount; i++) {
            garbageList.add(garbageFactory.createEntity(garbageType));
        }
        return garbageList;
    }
}
