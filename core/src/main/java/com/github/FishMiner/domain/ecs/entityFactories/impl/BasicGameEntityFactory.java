package com.github.FishMiner.domain.ecs.entityFactories.impl;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.components.*;
import com.github.FishMiner.domain.ecs.entityFactories.FishTypes;
import com.github.FishMiner.domain.ecs.entityFactories.IGameEntityFactory;

import java.util.Stack;

public class BasicGameEntityFactory implements IGameEntityFactory {
    Configuration config = Configuration.getInstance();

    @Override
    public Stack<Entity> createFish(FishTypes fishType, int amount) {
        Stack<Entity> fishList = new Stack<Entity>();

        for (int i = 0; i < amount; i++) {
            Entity fish = FishFactory.createEntity(
                fishType.getTexturePath(),
                fishType.getDepthLevel(),
                fishType.getSpeed(),
                fishType.getWeight()
            );
            fishList.add(fish);
        }
        return fishList;
    }

    @Override
    public Entity createHook() {
        return HookFactory.createEntity(config.getScreenWidth() / 2,  (int) (config.getScreenHeight() * config.getOceanHeightPercentage()));
    }

    @Override
    public Entity createSinker() {
        Entity sinker = new Entity();
        sinker.add(new AttachmentComponent(new Vector2(0, 0)));
        // rotation
        sinker.add(new RotationComponent(0f));
        // Texture and Animation
        TextureComponent textureComponent = new TextureComponent("hook.png");
        sinker.add(textureComponent);
        sinker.add(new WeightComponent(20));
        return sinker;
    }

    // Optional helper method if you need random positions
    private int getRandomHeightInRange(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
