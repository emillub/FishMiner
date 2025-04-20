package com.github.FishMiner.domain.factories.playerFactory;

import static com.github.FishMiner.domain.factories.ReelTypes.BASIC_REEL;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.github.FishMiner.common.Assets;
import com.github.FishMiner.common.Logger;
import com.github.FishMiner.domain.ecs.components.*;
import com.github.FishMiner.common.ValidateUtil;
import com.github.FishMiner.domain.ecs.utils.DomainUtils;
import com.github.FishMiner.domain.factories.HookTypes;
import com.github.FishMiner.domain.factories.ReelTypes;
import com.github.FishMiner.domain.factories.SinkerTypes;
import com.github.FishMiner.domain.states.HookStates;

public class PlayerFactory {
    private static final String TAG = "PlayerFactory";

    public PlayerFactory() {
    }

    public static Entity addNewPlayerCharacterTo(PooledEngine engine, int posX, int posY) {
        Entity playerEntity = createPlayerEntity(engine, posX, posY);
        PlayerComponent playerComponent = playerEntity.getComponent(PlayerComponent.class);

        playerComponent.setHook(null);
        TransformComponent playerPos = playerEntity.getComponent(TransformComponent.class);
        Entity hookEntity = HookFactory.createEntity(engine, HookTypes.BASIC_HOOK,
                (int) playerPos.pos.z,
                playerComponent.hookAnchorPoint, playerEntity);
        updateHook(hookEntity, playerEntity, engine);

        playerComponent.setReel(null);
        Entity reelEntity = ReelFactory.createEntity(engine, BASIC_REEL);
        updateReel(reelEntity, playerEntity, engine);

        playerComponent.setSinker(null);
        Entity sinkerEntity = SinkerFactory.createEntity(engine, SinkerTypes.BASIC_SINKER);
        updateSinker(sinkerEntity, playerEntity, engine);

        engine.addEntity(playerEntity);

        return playerEntity;
    }


    private static Entity createPlayerEntity(PooledEngine engine, int posX, int posY) {
        Entity player = engine.createEntity();

        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);
        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
        PlayerComponent playerComponent = engine.createComponent(PlayerComponent.class);
        ScoreComponent scoreComponent = engine.createComponent(ScoreComponent.class);
        InventoryComponent inventoryComponent = engine.createComponent(InventoryComponent.class);

        textureComponent.setRegion(Assets.PLAYER_TEXTURE);
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

    public static Entity createHookEntity(PooledEngine engine, Entity player) {
        PlayerComponent playerComponent = player.getComponent(PlayerComponent.class);
        TransformComponent playerPos = player.getComponent(TransformComponent.class);
        InventoryComponent inventory = player.getComponent(InventoryComponent.class);

        HookTypes equippedType = HookTypes.valueOf(inventory.getEquippedHookName().toUpperCase());
        Entity hook = HookFactory.createEntity(engine, equippedType, (int) playerPos.pos.z, playerComponent.hookAnchorPoint, player);

        AttachmentComponent hookAttachment = hook.getComponent(AttachmentComponent.class);
        hookAttachment.setParentEntity(player);

        return hook;
    }

    public static Entity createSinkerEntity(PooledEngine engine, Entity player) {
        SinkerTypes sinkerType = SinkerTypes
                .valueOf(formatEnumName(player.getComponent(InventoryComponent.class).getEquippedSinkerName()));
        Entity sinker = SinkerFactory.createEntity(engine, sinkerType);
        AttachmentComponent sinkerAttachment = engine.createComponent(AttachmentComponent.class);
        Entity hook = player.getComponent(PlayerComponent.class).getHook();
        System.out.println("Hook: " + hook);
        sinkerAttachment.setParentEntity(hook);
        sinkerAttachment.offset.y = -25f;
        sinker.add(sinkerAttachment);
        return sinker;
    }

    public static void updateReel(Entity newReel, Entity player, PooledEngine engine) {
        PlayerComponent playerComponent = player.getComponent(PlayerComponent.class);
        if (playerComponent == null) {
            Logger.getInstance().log(TAG, "PlayerComponent is null");
            return;
        }

        ReelTypes reelType;
        Entity currentReel = playerComponent.getReel();

        if (currentReel == null) {
            reelType = ReelTypes.BASIC_REEL;
        } else {
            reelType = ReelTypes
                    .valueOf(DomainUtils
                            .formatEnumName(currentReel.getComponent(ReelComponent.class).name));
            currentReel.removeAll();
            engine.removeEntity(currentReel);
        }

        float anchorY = playerComponent.hookAnchorPoint.y;

        ReelComponent reelComponent = newReel.getComponent(ReelComponent.class);

        int[] interval = DomainUtils.getDepthIntervalFor(reelType.getLengthLevel());
        reelComponent.lineLength = anchorY - interval[1];

        Logger.getInstance().log("PlayerFactory", "Reel lineLength set to: " + reelComponent.lineLength
                + "Reel length is at depth: " + reelType.getLengthLevel());

        AttachmentComponent reelAttachment = newReel.getComponent(AttachmentComponent.class);
        reelAttachment.offset.x = -0.9f;
        reelAttachment.offset.y = -0.7f;
        reelAttachment.setParentEntity(player);
        playerComponent.setReel(newReel);
        engine.addEntity(newReel);
    }

    public static void updateHook(Entity newHook, Entity player, PooledEngine engine) {
        PlayerComponent playerComponent = player.getComponent(PlayerComponent.class);
        if (playerComponent == null) {
            Logger.getInstance().log(TAG, "PlayerComponent is null");
            return;
        }

        HookTypes hookType;
        Entity currentHook = playerComponent.getHook();
        if (currentHook == null) {
            hookType = HookTypes.BASIC_HOOK;
        } else {
            hookType = HookTypes
                    .valueOf(DomainUtils
                            .formatEnumName(currentHook.getComponent(HookComponent.class).name));
            currentHook.removeAll();
            engine.removeEntity(currentHook);
        }

        TransformComponent transformComponent = newHook.getComponent(TransformComponent.class);

        transformComponent.pos.z = player.getComponent(TransformComponent.class).pos.z + 1;
        newHook.getComponent(HookComponent.class).anchorPoint.set(playerComponent.hookAnchorPoint);

        AttachmentComponent hookAttachment = newHook.getComponent(AttachmentComponent.class);

        if (playerComponent.getReel() != null) {
            AttachmentComponent sinkerAttachement = playerComponent.getSinker()
                    .getComponent(AttachmentComponent.class);
            sinkerAttachement.setParentEntity(newHook);
            sinkerAttachement.offset.x = -25f;

        }

        hookAttachment.setParentEntity(player);
        playerComponent.setHook(newHook);
        engine.addEntity(newHook);
    }

    public static void updateSinker(Entity newSinker, Entity player, PooledEngine engine) {
        PlayerComponent playerComponent = player.getComponent(PlayerComponent.class);
        if (playerComponent == null) {
            Logger.getInstance().log(TAG, "PlayerComponent is null");
            return;
        }
        Entity hook = playerComponent.getHook();
        if (hook == null) {
            Logger.getInstance().log(TAG, "PlayerComponent has no hook");
            return;
        }

        SinkerTypes sinkerType;
        Entity currentSinker = playerComponent.getSinker();
        if (currentSinker == null) {
            sinkerType = SinkerTypes.BASIC_SINKER;
        } else {
            sinkerType = SinkerTypes
                    .valueOf(DomainUtils
                            .formatEnumName(currentSinker.getComponent(SinkerComponent.class).name));
            currentSinker.removeAll();
            engine.removeEntity(currentSinker);
        }
        AttachmentComponent sinkerAttachment = newSinker.getComponent(AttachmentComponent.class);
        sinkerAttachment.setParentEntity(hook);
        sinkerAttachment.offset.y = -25f;

        newSinker.add(sinkerAttachment);
        playerComponent.setSinker(newSinker);
        engine.addEntity(newSinker);
    }

    private static String formatEnumName(String name) {
        if (name == null) {
            return null;
        }
        return name.toUpperCase().replace(" ", "_");
    }
}
