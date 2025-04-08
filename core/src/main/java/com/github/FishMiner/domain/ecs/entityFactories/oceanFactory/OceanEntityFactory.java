package com.github.FishMiner.domain.ecs.entityFactories.oceanFactory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.Logger;
import com.github.FishMiner.domain.ecs.entityFactories.FishTypes;
import com.github.FishMiner.domain.ecs.entityFactories.GarbageTypes;
import com.github.FishMiner.domain.ecs.entityFactories.IEntityType;
import com.github.FishMiner.domain.ecs.entityFactories.IOceanEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.SharkTypes;

import java.util.LinkedList;



public class OceanEntityFactory implements IOceanEntityFactory {
    private static final String TAG = "OceanEntityFactory";

    private final PooledEngine engine;
    private final FishFactory fishFactory;
    private  final GarbageFactory garbageFactory;
    private final SharkFactory sharkFactory;

    public OceanEntityFactory(PooledEngine engine) {
        this.engine = engine;
        this.fishFactory = new FishFactory(engine);
        this.garbageFactory = new GarbageFactory(engine);
        this.sharkFactory = new SharkFactory(engine);
    }

    @Override
    public Entity createEntity(IEntityType type) {
        if (type.getClass().equals(SharkTypes.class)) {
            return createSharks((SharkTypes) type, 1).get(0);
        } else if (type.getClass().equals(FishTypes.class)) {
            return createFish((FishTypes) type, 1).get(0);
        } else {
            IllegalArgumentException e = new IllegalArgumentException("Unsupported entity type: " + type);
            Logger.getInstance().error(TAG, e.getMessage(), e);
            throw e;
        }
    }


    @Override
    public LinkedList<Entity> createFish(FishTypes fishType, int amount) {
        LinkedList<Entity> fishList = new LinkedList<>();
        for (int i = 0; i < amount; i++) {
            fishList.add(fishFactory.createEntity(fishType));
        }
        return fishList;
    }

    @Override
    public LinkedList<Entity> createGarbage(GarbageTypes garbageType, int amount) {
        LinkedList<Entity> garbageList = new LinkedList<>();
        for (int i = 0; i < amount; i++) {
            garbageList.add(garbageFactory.createEntity(garbageType));
        }
        return garbageList;
    }

    @Override
    public LinkedList<Entity> createSharks(SharkTypes sharkType, int amount) {
        LinkedList<Entity> fishList = new LinkedList<>();
        for (int i = 0; i < amount; i++) {
            fishList.add(sharkFactory.createEntity(sharkType));
        }
        return fishList;
    }

}
