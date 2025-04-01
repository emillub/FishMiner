package com.github.FishMiner.domain.ecs.entityFactories.oceanFactory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.components.*;
import com.github.FishMiner.domain.ecs.entityFactories.GarbageTypes;
import com.github.FishMiner.domain.ecs.util.FishUtils;
import com.github.FishMiner.domain.states.FishableObjectStates;

public class GarbageFactory {

    private final PooledEngine engine;

    public GarbageFactory(PooledEngine engine) {
        this.engine = engine;
    }

    public Entity createEntity(GarbageTypes type) {
        int[] allowedDepths = type.getAllowedDepthLevels();
        int chosenDepthLevel = allowedDepths[MathUtils.random(allowedDepths.length - 1)];

        return createEntity(
            type.getTexturePath(),
            type.getFrameCols(),
            type.getFrameRows(),
            chosenDepthLevel,
            type.getWeight()
        );
    }

    @SuppressWarnings("unchecked")
    private Entity createEntity(String texturePath, int frameCols, int frameRows, int depthLevel, int weight) {
        Entity garbage = engine.createEntity();

        TransformComponent transform = engine.createComponent(TransformComponent.class);
        VelocityComponent velocity = engine.createComponent(VelocityComponent.class);
        BoundsComponent bounds = engine.createComponent(BoundsComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        AttachmentComponent attachment = engine.createComponent(AttachmentComponent.class);
        StateComponent<FishableObjectStates> state = engine.createComponent(StateComponent.class);
        AnimationComponent animation = engine.createComponent(AnimationComponent.class);
        WeightComponent weightComp = engine.createComponent(WeightComponent.class);
        FishComponent fishLike = engine.createComponent(FishComponent.class); // reusing FishComponent

        texture.setRegion(texturePath, frameCols, frameRows);

        fishLike.setDepthLevel(depthLevel);
        fishLike.setWeight(weight);
        fishLike.setBaseSpeed(1.5f); // slow drift
        float scale = 0.08f;
        fishLike.width = (int) (texture.getFrameWidth() * scale);
        fishLike.height = (int) (texture.getFrameHeight() * scale);

        weightComp.weight = weight;

        boolean movesRight = MathUtils.randomBoolean();
        float screenWidth = Configuration.getInstance().getScreenWidth();
        float startX = movesRight ? -fishLike.width : screenWidth + fishLike.width;
        float startY = FishUtils.getRandomDepthFor(depthLevel, fishLike.height);

        transform.pos = new Vector3(startX, startY, 0);
        transform.scale.set(scale, scale);
        velocity.velocity.x = FishUtils.getFishDirectionX(movesRight, 1.5f);

        bounds.bounds.set(startX, startY, fishLike.width, fishLike.height);
        attachment.offset.x = 10;

        state.changeState(FishableObjectStates.FISHABLE);
        animation.addAnimation(FishableObjectStates.FISHABLE.getAnimationKey(), texture, 0);
        animation.addAnimation(FishableObjectStates.HOOKED.getAnimationKey(), texture, 0, Animation.PlayMode.LOOP);
        animation.addAnimation(FishableObjectStates.REELING.getAnimationKey(), texture, 0);
        animation.setCurrentAnimation(FishableObjectStates.FISHABLE.getAnimationKey());

        garbage.add(transform);
        garbage.add(texture);
        garbage.add(velocity);
        garbage.add(bounds);
        garbage.add(attachment);
        garbage.add(state);
        garbage.add(animation);
        garbage.add(weightComp);
        garbage.add(fishLike); // yes, still needed for catching

        return garbage;
    }
}
