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
import com.github.FishMiner.domain.ecs.entityFactories.FishTypes;
import com.github.FishMiner.domain.ecs.util.FishUtils;
import com.github.FishMiner.domain.states.FishableObjectStates;

public class FishFactory {

    public static final int EURO_FACTOR = 10;

    public static Entity createEntity(FishTypes type) {
        int[] allowedDepths = type.getAllowedDepthLevels();
        int chosenDepthLevel = allowedDepths[MathUtils.random(allowedDepths.length - 1)];

        System.out.println("Spawning " + type.name() + " in depth level " + chosenDepthLevel);

        return createEntity(
            type.getTexturePath(),
            type.getFrameCols(),
            type.getFrameRows(),
            chosenDepthLevel,
            type.getSpeed(),
            type.getWeight(),
            type
        );
    }

    protected static Entity createEntity(String texturePath, int frameCols, int frameRows, int depthLevel, float speed, int weight, FishTypes type) {
        Entity fish = new Entity();

        int fishValueInEuros = calculateFishValue(depthLevel, (int)speed, weight);
        fish.add(new FishComponent(1.0f, fishValueInEuros));

        TextureComponent textureComponent = new TextureComponent(texturePath, frameCols, frameRows);
        fish.add(textureComponent);

        boolean movesRight = MathUtils.randomBoolean();
        float fishHeight = textureComponent.getRegion().getRegionHeight();
        float startX = FishUtils.getFishStartPosX(movesRight, textureComponent.getFrameWidth());
        float adjustedVelocity = FishUtils.getFishDirectionX(movesRight, speed);

        Vector2 initialPosition = new Vector2(
            startX,
            FishUtils.getRandomDepthFor(depthLevel, fishHeight)
        );

        PositionComponent fishPos = new PositionComponent(initialPosition);
        fish.add(fishPos);

        fish.add(new VelocityComponent(new Vector2(adjustedVelocity, 0)));

        BoundsComponent bc = new BoundsComponent();
        bc.bounds.setPosition(fishPos.position);
        bc.bounds.setWidth(textureComponent.getRegion().getRegionWidth());
        bc.bounds.setHeight(textureComponent.getFrameHeight());
        fish.add(bc);

        fish.add(new RotationComponent(0f));
        fish.add(new AttachmentComponent(new Vector2(10, 0)));
        fish.add(new StateComponent<>(FishableObjectStates.FISHABLE));

        AnimationComponent animationComponent = new AnimationComponent();
        animationComponent.addAnimation(FishableObjectStates.FISHABLE.getAnimationKey(), textureComponent, 0);
        animationComponent.addAnimation(FishableObjectStates.HOOKED.getAnimationKey(), textureComponent, 1, Animation.PlayMode.NORMAL);
        animationComponent.addAnimation(FishableObjectStates.REELING.getAnimationKey(), textureComponent, 1);
        animationComponent.setCurrentAnimation(FishableObjectStates.FISHABLE.getAnimationKey());

        animationComponent.setCurrentAnimation(FishableObjectStates.HOOKED.getAnimationKey());
        fish.add(animationComponent);

        return fish;
    }

    private int getRandomHeightInRange(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static int calculateFishValue(int depth, int speed, int weight) {
        float value = (float) (depth * speed * weight) / 100;
        return Math.round(value * EURO_FACTOR);
    }
}
