package com.github.FishMiner.domain.ecs.entityFactories.impl;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
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

        boolean movesRight = MathUtils.randomBoolean();

        float yPos = MathUtils.random(100f, config.getOceanHeight() - 100f);

        float xPos;
        if (movesRight) {
            xPos = 0;
        } else {
            xPos = config.getScreenWidth();
        }
        Vector2 spawnPosition = new Vector2(xPos, yPos);
        fish.add(new PositionComponent(spawnPosition));

        float speed = MathUtils.random(20f, 60f);

        float xVelocity = movesRight ? speed : -speed;
        Vector2 velocity = new Vector2(xVelocity, 0);
        fish.add(new VelocityComponent(velocity));

        fish.add(new FishComponent(32, 100));
        fish.add(new RotationComponent(0f));

        TextureComponent textureComponent = new TextureComponent("clown_fish_9row_3col.png", 3, 9);
        fish.add(textureComponent);

        fish.add(new AttachmentComponent(new Vector2(10, 0)));

        AnimationComponent animationComponent = new AnimationComponent();
        animationComponent.addAnimation("fishable", textureComponent, 0);
        animationComponent.addAnimation("hooked", textureComponent, 1, Animation.PlayMode.NORMAL);
        animationComponent.addAnimation("reeling", textureComponent, 2);
        animationComponent.setCurrentAnimation("fishable");
        fish.add(animationComponent);

        fish.add(new StateComponent<>(EntityState.FishStates.FISHABLE));

        // Make the bounding boxes match each fish’s single frame size
        int frameWidth = textureComponent.getFrameWidth();
        int frameHeight = textureComponent.getFrameHeight();

        BoundsComponent bounds = new BoundsComponent(
            spawnPosition,
            frameWidth,
            frameHeight
        );

        fish.add(bounds);

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
