package com.github.FishMiner.domain.factories.oceanFactory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.github.FishMiner.common.Configuration;
import com.github.FishMiner.common.Logger;
import com.github.FishMiner.domain.ecs.components.AnimationComponent;
import com.github.FishMiner.domain.ecs.components.AttachmentComponent;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.FishableComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.TextureComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;
import com.github.FishMiner.domain.ecs.components.WeightComponent;
import com.github.FishMiner.domain.factories.FishTypes;
import com.github.FishMiner.domain.ecs.utils.DomainUtils;
import com.github.FishMiner.common.ValidateUtil;
import com.github.FishMiner.domain.states.FishableObjectStates;

public class FishFactory {
    private static final String TAG = "FishFactory";
    public static final int EURO_FACTOR = 10;
    private final PooledEngine engine;

    protected FishFactory(PooledEngine engine) {
        this.engine = engine;
    }

    protected Entity createEntity(FishTypes type) {
        try {
            int[] allowedDepths = type.getAllowedDepthLevels();
            int chosenDepthLevel = allowedDepths[MathUtils.random(allowedDepths.length - 1)];

            validateFishFields(
                type.getTexturePath(),
                type.getFrameCols(),
                type.getFrameRows(),
                chosenDepthLevel,
                type.getSpeed(),
                type.getWeight(),
                type.getScale()
            );
            return createEntity(
                type.getTexturePath(),
                type.getFrameCols(),
                type.getFrameRows(),
                chosenDepthLevel,
                type.getSpeed(),
                type.getWeight(),
                type.getScale()
            );
        } catch (IllegalArgumentException e) {
            Logger.getInstance().error(TAG, "Invalid value(s) for createEntity: " + e.getMessage(), e);
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    private Entity createEntity(String texturePath, int frameCols, int frameRows, int depthLevel, float speed, int weight, float scale) {
        Entity fish = engine.createEntity();

        FishableComponent fishComponent = engine.createComponent(FishableComponent.class);
        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
        VelocityComponent velocityComponent = engine.createComponent(VelocityComponent.class);
        BoundsComponent boundsComponent = engine.createComponent(BoundsComponent.class);
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);
        AttachmentComponent attachmentComponent = engine.createComponent(AttachmentComponent.class);
        StateComponent<FishableObjectStates> stateComponent = engine.createComponent(StateComponent.class);
        AnimationComponent animationComponent = engine.createComponent(AnimationComponent.class);
        WeightComponent weightComponent = engine.createComponent(WeightComponent.class);

        textureComponent.setRegion(texturePath, frameCols, frameRows);
        fishComponent.setDepthLevel(depthLevel);
        fishComponent.setWeight(weight);
        fishComponent.setBaseSpeed(speed);
        fishComponent.width = textureComponent.getFrameWidth();
        fishComponent.height = textureComponent.getFrameHeight();

        weightComponent.weight = fishComponent.weight;

        boolean movesRight = MathUtils.randomBoolean();
        float screenWidth = Configuration.getInstance().getScreenWidth();

        // Start X: just offscreen left or right
        float startX = movesRight ? -fishComponent.width : screenWidth + fishComponent.width;

        // Start Y: based on depth
        float startY = DomainUtils.getRandomDepthFor(depthLevel, fishComponent.height);
        transformComponent.pos.set(startX, startY, 0);
        transformComponent.scale.set(scale, scale);
        velocityComponent.velocity.x = DomainUtils.getFishDirectionX(movesRight, speed);

        boundsComponent.bounds.set(
            transformComponent.pos.x,
            transformComponent.pos.y,
            fishComponent.width,
            fishComponent.height
        );

        attachmentComponent.offset.x = 10;

        stateComponent.changeState(FishableObjectStates.FISHABLE);

        animationComponent.addAnimation(FishableObjectStates.FISHABLE.getAnimationKey(), textureComponent, 0);
        animationComponent.addAnimation(FishableObjectStates.HOOKED.getAnimationKey(), textureComponent, 0, Animation.PlayMode.NORMAL);
        animationComponent.addAnimation(FishableObjectStates.REELING.getAnimationKey(), textureComponent, 0);
        animationComponent.addAnimation(FishableObjectStates.CAPTURED.getAnimationKey(), textureComponent, 0);
        animationComponent.setCurrentAnimation(FishableObjectStates.FISHABLE.getAnimationKey());

        fish.add(transformComponent);
        fish.add(textureComponent);
        fish.add(velocityComponent);
        fish.add(boundsComponent);
//        fish.add(attachmentComponent);
        fish.add(stateComponent);
        fish.add(animationComponent);
        fish.add(weightComponent);
        fish.add(fishComponent);

        return fish;
    }

    public static int calculateFishValue(int depth, int speed, int weight) {
        if (depth <= 0) depth = 1; // Preventing 0-value fish by ensuring depth is at least 1!
        float value = (float) (depth * speed * weight) / 100;
        return Math.round(value * EURO_FACTOR);
    }

    private void validateFishFields(String texturePath, int frameCols, int frameRows, int depthLevel, float speed, int weight, float scale) {
        ValidateUtil.validateNotNull(texturePath, "texturePath");
        ValidateUtil.validatePositiveInt(depthLevel, "depthLevel");
        ValidateUtil.validatePositiveInt(frameCols, "frameCols");
        ValidateUtil.validatePositiveInt(frameRows, "frameRows");
        ValidateUtil.validatePositiveInt(weight, "weight");
        ValidateUtil.validatePositiveFloat(speed, "speed");
        ValidateUtil.validatePositiveFloat(scale, "scale");
    }
}
