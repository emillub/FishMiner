package com.github.FishMiner.domain.factories.playerFactory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;

import com.github.FishMiner.domain.ecs.components.AttachmentComponent;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.InventoryComponent;
import com.github.FishMiner.domain.ecs.components.PlayerComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.RotationComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.TextureComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;

import com.github.FishMiner.domain.ecs.components.*;

import com.github.FishMiner.common.ValidateUtil;
import com.github.FishMiner.domain.states.HookStates;

public class PlayerFactory {
    private static final String TAG = "PlayerFactory";
    private static Entity player;

    private PlayerFactory() {
    }

    public static void addNewPlayerCharacterTo(PooledEngine engine, int posX, int posY) {
        Entity playerEntity = createPlayerEntity(engine, posX, posY);
        Entity hook = createHookEntity(engine, playerEntity);

        PlayerComponent playerComponent = playerEntity.getComponent(PlayerComponent.class);
        playerComponent.setHook(hook);

        playerEntity.add(playerComponent);

        engine.addEntity(playerEntity);
        engine.addEntity(hook);
        player = playerEntity;
    }

    private static Entity createPlayerEntity(PooledEngine engine, int posX, int posY) {
        Entity player = engine.createEntity();

        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);
        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
        PlayerComponent playerComponent = engine.createComponent(PlayerComponent.class);
        ScoreComponent scoreComponent = engine.createComponent(ScoreComponent.class);
        InventoryComponent inventoryComponent = engine.createComponent(InventoryComponent.class);

        textureComponent.setRegion("fisherman.png");
        transformComponent.pos.x = posX;
        transformComponent.pos.y = posY + textureComponent.getFrameHeight() * 0.3f;
        transformComponent.pos.z = 1;

        playerComponent.hookAnchorPoint.x = transformComponent.pos.x + textureComponent.getFrameWidth() * 0.5f;
        playerComponent.hookAnchorPoint.y = transformComponent.pos.y + textureComponent.getFrameHeight() * 0.5f;
        playerComponent.hookAnchorPoint.z = transformComponent.pos.z + 1;

        player.add(transformComponent);
        player.add(textureComponent);
        player.add(playerComponent);
        player.add(scoreComponent);
        player.add(inventoryComponent);

        return player;
    }

    /**
     * Note that we attempt to not set the position (transformComp) here.
     * Ideally, this should be done with the AttachmentComponent
     * Must be attached to a player entity
     * @return A hook Entity
     */
    @SuppressWarnings("unchecked")
    private static Entity createHookEntity(PooledEngine engine, Entity player) {
        Entity hook = engine.createEntity();

        HookComponent hookComponent = engine.createComponent(HookComponent.class);
        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
        RotationComponent rotationComponent = engine.createComponent(RotationComponent.class);
        BoundsComponent boundsComponent = engine.createComponent(BoundsComponent.class);
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);
        VelocityComponent velocityComponent = engine.createComponent(VelocityComponent.class);
        StateComponent<HookStates> stateComponent = engine.createComponent(StateComponent.class);
        AttachmentComponent attachmentComponent = engine.createComponent(AttachmentComponent.class);

        textureComponent.setRegion("hook_1cols_1rows.png");


        stateComponent.changeState(HookStates.SWINGING);
        velocityComponent.velocity = new Vector2(0, 0);

        attachmentComponent.setParentEntity(player);
        PlayerComponent playerComponent = player.getComponent(PlayerComponent.class);
        TransformComponent playerPos = player.getComponent(TransformComponent.class);

        hookComponent.anchorPoint.set(playerComponent.hookAnchorPoint);
        transformComponent.pos.z = playerPos.pos.z + 1;

        boundsComponent.bounds.setPosition(
            transformComponent.pos.x - boundsComponent.bounds.width * 0.5f,
            transformComponent.pos.y - boundsComponent.bounds.height * 0.5f
        );

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

        return hook;
    }

    public static Entity getPlayer() {
        ValidateUtil.validateNotNull(player, TAG + " -> player");
        return player;
    }
}
