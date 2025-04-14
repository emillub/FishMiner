package com.github.FishMiner.domain.factories.playerFactory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.github.FishMiner.common.Logger;
import com.github.FishMiner.common.ValidateUtil;
import com.github.FishMiner.domain.ecs.components.*;
import com.github.FishMiner.domain.ecs.utils.DomainUtils;
import com.github.FishMiner.domain.factories.ReelTypes;
import com.github.FishMiner.domain.states.HookStates;

public class ReelFactory {
    private static final String TAG = "ReelFactory";

    private ReelFactory() {}

    protected static Entity createEntity(
        PooledEngine engine,
        String name,
        float returnSpeed,
        int depthLevel,
        String texturePath,
        int frameCols,
        int frameRows,
        float scale,
        int price
    ) {
        Entity reel = engine.createEntity();

        ReelComponent reelComponent = engine.createComponent(ReelComponent.class);
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);
        StateComponent<HookStates> stateComponent = engine.createComponent(StateComponent.class);
        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
        AnimationComponent animationComponent = engine.createComponent(AnimationComponent.class);
        AttachmentComponent attachmentComponent = engine.createComponent(AttachmentComponent.class);
        RotationComponent rotationComponent = engine.createComponent(RotationComponent.class);
        UpgradeComponent upgradeComponent = engine.createComponent(UpgradeComponent.class);

        textureComponent.setRegion(texturePath, frameCols, frameRows);
        transformComponent.pos.set(360f, 630f, 1f);
        transformComponent.scale.set(1f, 1f);

        reelComponent.name = name;
        reelComponent.returnSpeed = returnSpeed;
        reelComponent.lineLength = DomainUtils.getLengthDepthFor(depthLevel, textureComponent.getFrameHeight());

        stateComponent.changeState(HookStates.SWINGING);

        animationComponent.addAnimation(HookStates.REELING.getAnimationKey(), textureComponent, 0, Animation.PlayMode.LOOP);
        animationComponent.addAnimation(HookStates.FIRE.getAnimationKey(), textureComponent, 0, Animation.PlayMode.LOOP);
        animationComponent.addAnimation(HookStates.RETURNED.getAnimationKey(), textureComponent, 0, Animation.PlayMode.LOOP);
        animationComponent.addSingleFrameAnimation(HookStates.SWINGING.getAnimationKey(), textureComponent, 0, 0);
        animationComponent.setCurrentAnimation(HookStates.SWINGING.getAnimationKey());

        if (price <= 0) {
            upgradeComponent.setUpgraded(true);
        } else {
            upgradeComponent.setType(reel);
            upgradeComponent.setPrice(price);
        }

        if (reel.getComponent(TransformComponent.class) != null) {
            throw new IllegalStateException("Reel already has TransformComponent!");
        }

        reel.add(reelComponent);
        reel.add(textureComponent);
        reel.add(stateComponent);
        reel.add(transformComponent);
        reel.add(animationComponent);
        reel.add(attachmentComponent);
        reel.add(rotationComponent);
        reel.add(upgradeComponent);

        return reel;
    }

    public static Entity createEntity(PooledEngine engine, ReelTypes type) {
        validateReelType(type);
        return createEntity(
            engine,
            type.getName(),
            type.getReturnSpeed(),
            type.getLengthLevel(),
            type.getTexturePath(),
            type.getFrameCols(),
            type.getFrameRows(),
            type.getScale(),
            type.getPrice()
        );
    }

    private static void validateReelType(ReelTypes reelType) {
        if (reelType.getName().isBlank()) {
            IllegalArgumentException exception = new IllegalArgumentException("reelType name cannot be blank");
            Logger.getInstance().error(TAG, "Missing name in reelType", exception);
            throw exception;
        }
        ValidateUtil.validateNotNull(reelType.getTexturePath(), "reelType");
        ValidateUtil.validatePositiveInt(reelType.getLengthLevel(), "reelType.getLengthLevel()");
        ValidateUtil.validatePositiveFloat(reelType.getScale(), "reelType.getScale()");
    }
}
