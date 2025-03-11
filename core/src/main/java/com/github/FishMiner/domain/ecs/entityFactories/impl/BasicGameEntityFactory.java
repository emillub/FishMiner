package com.github.FishMiner.domain.ecs.entityFactories.impl;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.components.*;
import com.github.FishMiner.domain.ecs.entityFactories.IGameEntityFactory;
import com.github.FishMiner.domain.states.EntityState;

public class BasicGameEntityFactory implements IGameEntityFactory {

    @Override
    public Entity createFish() {
        Entity fish = new Entity();
        Configuration config = Configuration.getInstance();

        // sets fish size and value
        fish.add(new FishComponent(32, 100));

        // Position
        int lowerSpawnPoint = config.getOceanHeight() / 2;
        int clownFishSpawnHeight = config.getOceanHeight() - 100;
        Vector2 initialPosition = new Vector2(config.getScreenWidth(), clownFishSpawnHeight);
        PositionComponent fishPos = new PositionComponent(initialPosition);
        fish.add(fishPos);

        // rotation
        fish.add(new RotationComponent(0f));

        // Texture and Animation
        TextureComponent textureComponent = new TextureComponent("clown_fish_9row_3col.png", 3, 9);
        fish.add(textureComponent);

        // Attachment to Hook
        fish.add(new AttachmentComponent(new Vector2(10, 0)));

        // Animation
        AnimationComponent animationComponent = new AnimationComponent();
        animationComponent.addAnimation("fishable", textureComponent, 0);
        animationComponent.addAnimation("hooked", textureComponent, 1, Animation.PlayMode.NORMAL);
        animationComponent.addAnimation("reeling", textureComponent, 2);
        animationComponent.setCurrentAnimation("hooked");
        fish.add(animationComponent);

        // State component
        fish.add(new StateComponent<>(EntityState.FishStates.FISHABLE));

        // Velocity
        fish.add(new VelocityComponent(new Vector2(-60, 0)));

        // Bounds for collision detection
        fish.add(new BoundsComponent(
            fishPos.position,
            textureComponent.getRegion().getRegionWidth(),
            textureComponent.getRegion().getRegionHeight()
        ));

        return fish;
    }


    @Override
    public Entity createHook() {
        Entity hook = new Entity();
        Configuration config = Configuration.getInstance();

        hook.add(new HookComponent(0.5f, 0.5f));

        // Position
        Vector2 initialPosition = new Vector2((float) config.getScreenWidth() / 2, config.getOceanHeight() - 100);
        PositionComponent positionComponent = new PositionComponent(initialPosition);
        hook.add(positionComponent);

        // rotation
        hook.add(new RotationComponent(0f));

        // Texture and Animation
        TextureComponent textureComponent = new TextureComponent("hook.png");
        hook.add(textureComponent);

        // Velocity
        hook.add(new VelocityComponent(new Vector2(0, 0)));

        // Bounds for collision detection
        hook.add(new BoundsComponent(initialPosition, textureComponent.getRegion().getRegionWidth(), textureComponent.getRegion().getRegionHeight()));

        // Add a StateComponent with a default state (SWINGING)
        hook.add(new StateComponent<>(EntityState.HookStates.FIRE));

        return hook;
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
