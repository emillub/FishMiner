package com.github.FishMiner.domain.factories.playerFactory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.github.FishMiner.domain.ecs.components.AttachmentComponent;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.PlayerComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.RotationComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.TextureComponent;
import com.github.FishMiner.domain.ecs.components.UpgradeComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;
import com.github.FishMiner.domain.factories.HookTypes;
import com.github.FishMiner.domain.states.HookStates;

/**
 * This is not used any longer. Only needed for testing, so do not use it otherwise.
 * @PlayerFactory replaces this class.
 */
public class HookFactory {

    private HookFactory() {

    }

    public static Entity createEntity(PooledEngine engine, HookTypes type, int posZ, Vector3 anchorPoint) {
        validateHookType(type);

        String texturePath = type.getTexturePath();
        String name = type.getName();
        int cols = type.getFrameCols();
        int rows = type.getFrameRows();
        int price = type.getPrice();
        float scale = type.getScale();
        float precision = type.getPrecision();

        Entity hook = engine.createEntity();

        HookComponent hookComponent = engine.createComponent(HookComponent.class);
        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
        RotationComponent rotationComponent = engine.createComponent(RotationComponent.class);
        BoundsComponent boundsComponent = engine.createComponent(BoundsComponent.class);
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);
        VelocityComponent velocityComponent = engine.createComponent(VelocityComponent.class);
        StateComponent<HookStates> stateComponent = engine.createComponent(StateComponent.class);
        AttachmentComponent attachmentComponent = engine.createComponent(AttachmentComponent.class);
        UpgradeComponent upgradeComponent = engine.createComponent(UpgradeComponent.class);

        if (price == 0) {
            upgradeComponent.setUpgraded(true);
        } else {
            upgradeComponent.setType(hook);
            upgradeComponent.setPrice(price);
        }

        textureComponent.setRegion(texturePath, cols, rows);
        transformComponent.pos.z = posZ + 1;
        transformComponent.scale = new Vector2(scale, scale);
        hookComponent.anchorPoint.set(anchorPoint);


        velocityComponent.velocity = new Vector2(0, 0);
        stateComponent.changeState(HookStates.SWINGING);

        boundsComponent.bounds.setSize(
            textureComponent.getFrameWidth(),
            textureComponent.getFrameHeight()
        );

        hook.add(textureComponent);
        hook.add(hookComponent);
        hook.add(transformComponent);
        hook.add(rotationComponent);
        hook.add(velocityComponent);
        hook.add(stateComponent);
        hook.add(attachmentComponent);
        hook.add(boundsComponent);
        hook.add(upgradeComponent);

        return hook;
    }

    private static void validateHookType(HookTypes hookType) {
        if (hookType.getName().isBlank()) {
            throw new IllegalArgumentException("Hook name cannot be blank");
        }
        if (hookType.getTexturePath() == null) {
            throw new IllegalArgumentException("Texture path cannot be null");
        }
    }
}
