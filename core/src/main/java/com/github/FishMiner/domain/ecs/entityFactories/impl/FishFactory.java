package com.github.FishMiner.domain.ecs.entityFactories.impl;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.github.FishMiner.domain.ecs.components.AnimationComponent;
import com.github.FishMiner.domain.ecs.components.AttachmentComponent;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.FishComponent;
import com.github.FishMiner.domain.ecs.components.PositionComponent;
import com.github.FishMiner.domain.ecs.components.RotationComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.TextureComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;
import com.github.FishMiner.domain.ecs.util.FishUtils;
import com.github.FishMiner.domain.states.FishableObjectStates;


public class FishFactory {

    protected static Entity createEntity(String texturePath, int depthLevel, float speed, int weight) {
        Entity fish = new Entity();

        // Fish value is determined by depth level, the weight and speed of the fish
        int value = (int) ((float) depthLevel * weight * Math.abs(speed));
        fish.add(new FishComponent(1f, value));

        // Position
        boolean movesRight = MathUtils.randomBoolean();
        Vector2 initialPosition = new Vector2(
            FishUtils.getFishStartPosX(movesRight),
            FishUtils.getRandomDepthFor(depthLevel)
        );
        PositionComponent fishPos = new PositionComponent(initialPosition);
        fish.add(fishPos);

        // Velocity
        float adjustedVelocity = FishUtils.getFishDirectionX(movesRight, speed);
        fish.add(new VelocityComponent(new Vector2(adjustedVelocity,0)));

        // Texture and Animation
        TextureComponent textureComponent = new TextureComponent(texturePath);
        fish.add(textureComponent);

        // Bounds for collision detection
        fish.add(new BoundsComponent(fishPos.position, textureComponent.getRegion().getRegionWidth(), textureComponent.getRegion().getRegionHeight()));

        // rotation
        fish.add(new RotationComponent(0f));

        // Attachement to Hook
        fish.add(new AttachmentComponent(new Vector2(10, 0)));

        // State component
        fish.add(new StateComponent<>(FishableObjectStates.FISHABLE));

        // Animation
        AnimationComponent animationComponent = new AnimationComponent();
        animationComponent.addAnimation(FishableObjectStates.FISHABLE.getAnimationKey(), textureComponent, 0);
        animationComponent.addAnimation(FishableObjectStates.HOOKED.getAnimationKey(), textureComponent, 1, Animation.PlayMode.NORMAL);
        animationComponent.addAnimation(FishableObjectStates.REELING.getAnimationKey(), textureComponent, 2);

        animationComponent.setCurrentAnimation(FishableObjectStates.FISHABLE.getAnimationKey());
        fish.add(animationComponent);

        return fish;
    }



    private int getRandomHeightInRange(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
