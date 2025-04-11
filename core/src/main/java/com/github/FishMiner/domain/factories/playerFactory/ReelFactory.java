package com.github.FishMiner.domain.factories.playerFactory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.github.FishMiner.common.Logger;
import com.github.FishMiner.common.ValidateUtil;
import com.github.FishMiner.domain.ecs.components.AttachmentComponent;
import com.github.FishMiner.domain.ecs.components.AnimationComponent;
import com.github.FishMiner.domain.ecs.components.ReelComponent;
import com.github.FishMiner.domain.ecs.components.RotationComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.TextureComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.UpgradeComponent;
import com.github.FishMiner.domain.ecs.utils.DomainUtils;
import com.github.FishMiner.domain.factories.ReelTypes;
import com.github.FishMiner.domain.states.HookStates;

public class ReelFactory {
    private static final String TAG = "ReelFactory";

    private ReelFactory() {
    }

    // Direct creation using explicit parameters, akin to SinkerFactory.
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
        transformComponent.scale = new Vector2(scale, scale);
        transformComponent.pos.z = 1;

        reelComponent.name = name;
        reelComponent.returnSpeed = returnSpeed;
        reelComponent.lineLength = DomainUtils.getLengthDepthFor(depthLevel, textureComponent.getFrameHeight());

        stateComponent.changeState(HookStates.SWINGING);

        if (price == 0) {
            upgradeComponent.setUpgraded(true);
        } else {
            upgradeComponent.setType(reel);
            upgradeComponent.setPrice(price);
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
        String texturePath = type.getTexturePath();
        String name = type.getName();
        int frameCols = type.getFrameCols();
        int frameRows = type.getFrameRows();
        float returnSpeed = type.getReturnSpeed();
        int depthLevel = type.getLengthLevel();
        int price = type.getPrice();
        float scale = type.getScale();

        return createEntity(engine, name, returnSpeed, depthLevel, texturePath, frameCols, frameRows, scale, price);
    }

    private static void validateReelType(ReelTypes reelType) {
        if (reelType.getName().isBlank()) {
            IllegalArgumentException exception = new IllegalArgumentException("reelType name cannot be blank");
            Logger.getInstance().error(TAG, "woopsie, add name the the reelType", exception);
            throw exception;
        }
        ValidateUtil.validateNotNull(reelType.getTexturePath(), "reelType");
        ValidateUtil.validatePositiveInt(reelType.getLengthLevel(), "reelType.getLengthLevel()");
        ValidateUtil.validatePositiveFloat(reelType.getScale(), "reelType.getScale()");
    }
}
