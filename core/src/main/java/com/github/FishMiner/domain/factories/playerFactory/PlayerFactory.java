package com.github.FishMiner.domain.factories.playerFactory;

import static com.github.FishMiner.domain.factories.ReelTypes.BASIC_REEL;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
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

    private PlayerFactory() {}

    public static Entity addNewPlayerCharacterTo(PooledEngine engine, int posX, int posY) {
        Entity playerEntity = createPlayerEntity(engine, posX, posY);
        Entity hookEntity = createHookEntity(engine, playerEntity);
        Entity reelEntity = createReelEntity(engine, playerEntity);
        Entity sinkerEntity = createSinkerEntity(engine, hookEntity);

        PlayerComponent playerComponent = playerEntity.getComponent(PlayerComponent.class);
        playerComponent.setHook(hookEntity);
        playerComponent.setReel(reelEntity);

        engine.addEntity(playerEntity);
        engine.addEntity(hookEntity);
        engine.addEntity(reelEntity);
        engine.addEntity(sinkerEntity);

        return playerEntity;
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

    private static Entity createHookEntity(PooledEngine engine, Entity player) {
        PlayerComponent playerComponent = player.getComponent(PlayerComponent.class);
        TransformComponent playerPos = player.getComponent(TransformComponent.class);
        InventoryComponent inventory = player.getComponent(InventoryComponent.class);

        HookTypes equippedType = HookTypes.valueOf(inventory.getEquippedHookName().toUpperCase());
        Entity hook = HookFactory.createEntity(engine, equippedType, (int) playerPos.pos.z, playerComponent.hookAnchorPoint, player);

        AttachmentComponent hookAttachment = hook.getComponent(AttachmentComponent.class);
        hookAttachment.setParentEntity(player);

        return hook;
    }

    private static Entity createReelEntity(PooledEngine engine, Entity player) {
        TransformComponent playerPos = player.getComponent(TransformComponent.class);
        PlayerComponent playerComponent = player.getComponent(PlayerComponent.class);

        ReelTypes reelType = BASIC_REEL;
        float anchorY = playerComponent.hookAnchorPoint.y;

        Entity reel = ReelFactory.createEntity(engine, reelType);
        ReelComponent reelComponent = reel.getComponent(ReelComponent.class);

        int[] interval = DomainUtils.getDepthIntervalFor(reelType.getLengthLevel());
        reelComponent.lineLength = anchorY - interval[1];

        Logger.getInstance().log("PlayerFactory", "Reel lineLength set to: " + reelComponent.lineLength + "Reel length is at depth: " + reelType.getLengthLevel());

        AttachmentComponent reelAttachment = reel.getComponent(AttachmentComponent.class);
        reelAttachment.offset.x = -0.9f;
        reelAttachment.offset.y = -0.7f;
        reelAttachment.setParentEntity(player);

        return reel;
    }

    private static Entity createSinkerEntity(PooledEngine engine, Entity hook) {
        Entity sinker = SinkerFactory.createEntity(engine, SinkerTypes.HEAVY_SINKER);
        AttachmentComponent sinkerAttachment = engine.createComponent(AttachmentComponent.class);
        sinkerAttachment.setParentEntity(hook);
        sinkerAttachment.offset.y = -25f;
        sinker.add(sinkerAttachment);
        return sinker;
    }
}
