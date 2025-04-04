package com.github.FishMiner.domain.ecs.entityFactories.playerFactory;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
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
import com.github.FishMiner.domain.states.HookStates;

public class PlayerFactory {

    private PlayerFactory() {
    }

    public static void addNewPlayerCharacterTo(Engine engine, int posX, int posY, InventoryComponent existingInventory) {
        Entity player = createPlayerEntity(engine, posX, posY, existingInventory);
        Entity hook = createHookEntity(engine, player);

        PlayerComponent playerComponent = player.getComponent(PlayerComponent.class);
        playerComponent.hook = hook;

        player.add(playerComponent);

        engine.addEntity(player);
        engine.addEntity(hook);
    }

    private static Entity createPlayerEntity(Engine engine, int posX, int posY, InventoryComponent existingInventory) {
        Entity player = new Entity();

        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);
        PlayerComponent playerComponent = engine.createComponent(PlayerComponent.class);

        textureComponent.setRegion("fisherman.png");
        transformComponent.pos.x = posX;
        transformComponent.pos.y =  posY + textureComponent.getFrameHeight() * 0.3f;
        transformComponent.pos.z = 0f;


        // TODO: maybe player is added via the hook as an attachment? check this

        playerComponent.hookAnchorPoint.x = transformComponent.pos.x + textureComponent.getFrameWidth() * 0.5f;
        playerComponent.hookAnchorPoint.y = transformComponent.pos.y + textureComponent.getFrameHeight() * 0.5f;
        playerComponent.hookAnchorPoint.z = transformComponent.pos.z + 1;

        player.add(transformComponent);
        player.add(textureComponent);
        player.add(playerComponent);

        if (existingInventory != null) {
            player.add(existingInventory);  // Reusing previous inventory
        } else {
            InventoryComponent inventory = engine.createComponent(InventoryComponent.class);
            inventory.money = 0;
            player.add(inventory);         // Default if none provided
        }
        return player;
    }

    /**
     * Note that we attempt to not set the position (transformComp) here.
     * Ideally, this should be done with the AttachmentComponent
     * Must be attached to a player entity
     * @return A hook Entity
     */
    @SuppressWarnings("unchecked")
    private static Entity createHookEntity(Engine engine, Entity player) {
        Entity hook = new Entity();

        HookComponent hookComponent = engine.createComponent(HookComponent.class);
        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
        RotationComponent rotationComponent = engine.createComponent(RotationComponent.class);
        BoundsComponent boundsComponent = engine.createComponent(BoundsComponent.class);
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);
        VelocityComponent velocityComponent = engine.createComponent(VelocityComponent.class);
        StateComponent<HookStates> stateComponent = engine.createComponent(StateComponent.class);
        AttachmentComponent attachmentComponent = engine.createComponent(AttachmentComponent.class);

        textureComponent.setRegion("hook_1cols_1rows.png");
        // TODO: remove rotation and handle this with transformComponent
        hook.add(rotationComponent);

        stateComponent.changeState(HookStates.SWINGING);

        velocityComponent.velocity = new Vector2(0, 0);

        PlayerComponent playerComponent = player.getComponent(PlayerComponent.class);
        hookComponent.anchorPoint.set(playerComponent.hookAnchorPoint);

        // TODO: add scaling and centralize width and height via something other than textures
        boundsComponent.bounds.setPosition(
            transformComponent.pos.x - boundsComponent.bounds.width * 0.5f,
            transformComponent.pos.y - boundsComponent.bounds.height * 0.5f
        );

        boundsComponent.bounds.setSize(
            textureComponent.getFrameWidth(),
            textureComponent.getFrameHeight()
        );



        // Add a StateComponent with a default state (SWINGING)
        hook.add(textureComponent);
        hook.add(hookComponent);
        hook.add(transformComponent);
        hook.add(velocityComponent);
        hook.add(stateComponent);
        hook.add(attachmentComponent);
        hook.add(boundsComponent);

        return hook;
    }
}

