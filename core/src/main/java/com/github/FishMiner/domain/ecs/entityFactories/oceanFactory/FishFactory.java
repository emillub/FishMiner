package com.github.FishMiner.domain.ecs.entityFactories.oceanFactory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.Logger;
import com.github.FishMiner.domain.ecs.components.AnimationComponent;
import com.github.FishMiner.domain.ecs.components.AttachmentComponent;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.FishComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.TextureComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;
import com.github.FishMiner.domain.ecs.components.WeightComponent;
import com.github.FishMiner.domain.ecs.entityFactories.FishTypes;
import com.github.FishMiner.domain.ecs.util.FishUtils;
import com.github.FishMiner.domain.ecs.util.ValidateUtil;
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

        FishComponent fishComponent = engine.createComponent(FishComponent.class);
        TransformComponent transform = engine.createComponent(TransformComponent.class);
        VelocityComponent velocity = engine.createComponent(VelocityComponent.class);
        BoundsComponent bounds = engine.createComponent(BoundsComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        AttachmentComponent attachment = engine.createComponent(AttachmentComponent.class);
        StateComponent<FishableObjectStates> state = engine.createComponent(StateComponent.class);
        AnimationComponent animation = engine.createComponent(AnimationComponent.class);
        WeightComponent weightComp = engine.createComponent(WeightComponent.class);

        texture.setRegion(texturePath, frameCols, frameRows);
        fishComponent.setDepthLevel(depthLevel);
        fishComponent.setWeight(weight);
        fishComponent.setBaseSpeed(speed);
        fishComponent.width = texture.getFrameWidth();
        fishComponent.height = texture.getFrameHeight();

        weightComp.weight = fishComponent.weight;

        boolean movesRight = MathUtils.randomBoolean();
        float screenWidth = Configuration.getInstance().getScreenWidth();

        // Start X: just offscreen left or right
        float startX = movesRight ? -fishComponent.width : screenWidth + fishComponent.width;
        System.out.println("Fish moves " + (movesRight ? "right" : "left") + " from x=" + startX);

        // Start Y: based on depth
        float startY = FishUtils.getRandomDepthFor(depthLevel, fishComponent.height);
        transform.pos = new Vector3(startX, startY, 0);
        transform.scale.set(scale, scale);
        velocity.velocity.x = FishUtils.getFishDirectionX(movesRight, speed);

        bounds.bounds.set(
            transform.pos.x,
            transform.pos.y,
            fishComponent.width,
            fishComponent.height
        );

        attachment.offset.x = 10;

        state.changeState(FishableObjectStates.FISHABLE);

        animation.addAnimation(FishableObjectStates.FISHABLE.getAnimationKey(), texture, 0);
        animation.addAnimation(FishableObjectStates.HOOKED.getAnimationKey(), texture, 0, Animation.PlayMode.NORMAL);
        animation.addAnimation(FishableObjectStates.REELING.getAnimationKey(), texture, 0);
        animation.setCurrentAnimation(FishableObjectStates.FISHABLE.getAnimationKey());

        fish.add(transform);
        fish.add(texture);
        fish.add(velocity);
        fish.add(bounds);
        fish.add(attachment);
        fish.add(state);
        fish.add(animation);
        fish.add(weightComp);
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
