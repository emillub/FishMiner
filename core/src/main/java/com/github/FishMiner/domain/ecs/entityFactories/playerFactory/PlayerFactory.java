package com.github.FishMiner.domain.ecs.entityFactories.playerFactory;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.components.AttachmentComponent;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.RotationComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.TextureComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;
import com.github.FishMiner.domain.states.HookStates;

public class PlayerFactory {
    private final Engine engine;

    public PlayerFactory(Engine engine) {
        this.engine = engine;
    }

    public void addNewPlayerCharacterTo(Engine engine, int posX, int posY) {
        Entity player = createPlayerEntity(posX, posY);
        Entity hook = createHookEntity();

        AttachmentComponent hookAttachment = hook.getComponent(AttachmentComponent.class);
        hookAttachment.parent = player;
        hookAttachment.offset.y = 10;

        engine.addEntity(player);
        engine.addEntity(hook);
    }

    private Entity createPlayerEntity(int posX, int posY) {
        Entity player = new Entity();

        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);

        textureComponent.setRegion("fisherman.png");

        transformComponent.pos.x = posX;
        transformComponent.pos.y =  posY + textureComponent.getFrameHeight() * 0.3f;
        //transformComponent.scale = new Vector2(0.5f, 0.5f);


        // TODO: maybe player is added via the hook as an attachment? check this
        player.add(transformComponent);
        player.add(textureComponent);

        return player;
    }

    /**
     * Note that we attempt to not set the position (transformComp) here.
     * Ideally, this should be done with the AttachmentComponent
     * Must be attached to a player entity
     * @return A hook Entity
     */
    @SuppressWarnings("unchecked")
    private Entity createHookEntity() {
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


        //boundsComponent.bounds.setX(transformComponent.pos.x);
        //boundsComponent.bounds.setY(transformComponent.pos.y);
        //boundsComponent.bounds.setWidth(textureComponent.getFrameWidth());
        //boundsComponent.bounds.setHeight(textureComponent.getFrameHeight());


        // TODO: add scaling and centralize width and height via something other than textures
        boundsComponent.bounds.setSize(
            textureComponent.getFrameWidth(),
            textureComponent.getFrameHeight()
        );

        boundsComponent.bounds.setPosition(
            transformComponent.pos.x - boundsComponent.bounds.width * 0.5f,
            transformComponent.pos.y - boundsComponent.bounds.height * 0.5f
        );


        // Add a StateComponent with a default state (SWINGING)
        hook.add(hookComponent);
        hook.add(transformComponent);
        hook.add(velocityComponent);
        hook.add(boundsComponent);
        hook.add(stateComponent);
        hook.add(textureComponent);
        hook.add(attachmentComponent);

        return hook;
    }
}

