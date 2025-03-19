package com.github.FishMiner.domain.ecs.entityFactories.impl;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.components.*;
import com.github.FishMiner.domain.ecs.entityFactories.FishTypes;
import com.github.FishMiner.domain.ecs.entityFactories.IGameEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.playerFactory.HookFactory;

import java.util.LinkedList;


public class BasicGameEntityFactory implements IGameEntityFactory {
    Configuration config = Configuration.getInstance();

    @Override
    public LinkedList<Entity> createFish(FishTypes fishType, int amount) {
        LinkedList<Entity> fishList = new LinkedList<>();
        for (int i = 0; i < amount; i++) {
            fishList.add(FishFactory.createEntity(fishType));
        }
        return fishList;
    }


    @Override
    public Entity createHook() {
        return HookFactory.createEntity( (int) config.getScreenWidth() / 2,  (int) (config.getScreenHeight() * config.getOceanHeightPercentage()));
    }

    @Override
    public Entity createSinker() {
        Entity sinker = new Entity();
        sinker.add(new AttachmentComponent(new Vector2(0, 0)));
        // rotation
        sinker.add(new RotationComponent(0f));
        // Texture and Animation
        TextureComponent textureComponent = new TextureComponent("sinker_1cols_1rows.png", 1, 1);
        sinker.add(textureComponent);
        sinker.add(new WeightComponent(20));
        return sinker;
    }

    private int getRandomHeightInRange(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
