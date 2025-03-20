package com.github.FishMiner.domain.ecs.entityFactories.impl;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.math.Vector3;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.components.*;
import com.github.FishMiner.domain.ecs.entityFactories.FishTypes;
import com.github.FishMiner.domain.ecs.entityFactories.IGameEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.playerFactory.HookFactory;
import com.github.FishMiner.domain.ecs.systems.RotationSystem;

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


    @Override
    public Entity createHook() {
        return hookFactory.createEntity( (int) config.getScreenWidth() / 2,  (int) (config.getScreenHeight() * config.getOceanHeightPercentage()));
    }

    @Override
    public Entity createSinker() {
        Entity sinker = new Entity();
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);
        AttachmentComponent attachmentComponent = engine.createComponent(AttachmentComponent.class);
        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
        WeightComponent weightComponent = engine.createComponent(WeightComponent.class);

        textureComponent.setRegion("sinker_1cols_1rows.png", 1, 1);
        transformComponent.pos = new Vector3(100, 100, 1);
        weightComponent.weight = 10;

        sinker.add(textureComponent);
        sinker.add(attachmentComponent);
        sinker.add(transformComponent);
        sinker.add(weightComponent);

        return sinker;
    }
}
