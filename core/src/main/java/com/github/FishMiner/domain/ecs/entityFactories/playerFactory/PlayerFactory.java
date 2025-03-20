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

    public void addPlayerCharacter(Engine engine, int posX, int posY) {
        Configuration config = Configuration.getInstance();
        Entity player = createPlayer(
            config.getScreenWidth() / 2,
            (int) (config.getScreenHeight() * config.getOceanHeightPercentage())
        );

        Entity hook = createHook(player);

        engine.addEntity(player);
        engine.addEntity(hook);
    }

    private Entity createPlayer(int posX, int posY) {
        Entity player = new Entity();

        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);

        transformComponent.pos.x = posX;
        transformComponent.pos.y =  posY;

        textureComponent.setRegion("fisherman.png");

        player.add(transformComponent);
        player.add(textureComponent);

        return player;
    }

    /**
     * Note that we attempt to not set the position (transformComp) here.
     * Ideally, this should be done with the AttachmentComponent
     * @param player The entity that holds the hook
     * @return A hook Entity
     */
    private Entity createHook(Entity player) {
        Entity hook = new Entity();

        HookComponent hookComponent = engine.createComponent(HookComponent.class);
        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
        RotationComponent rotationComponent = engine.createComponent(RotationComponent.class);
        BoundsComponent boundsComponent = engine.createComponent(BoundsComponent.class);
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);
        VelocityComponent velocityComponent = engine.createComponent(VelocityComponent.class);
        StateComponent<HookStates> stateComponent = engine.createComponent(StateComponent.class);
        AttachmentComponent attachmentComponent = engine.createComponent(AttachmentComponent.class);

        // TODO: remove rotation and handle this with transformComponent
        hook.add(rotationComponent);

        stateComponent.changeState(HookStates.SWINGING);

        velocityComponent.velocity = new Vector2(0, 0);

        textureComponent.setRegion("hook_1cols_1rows.png");
        System.out.println("hook_1cols_1rows.png set");
        System.out.println(textureComponent.texturePath);

        // TODO: fix misplaced bounds
        boundsComponent.bounds.setX(transformComponent.pos.x);
        boundsComponent.bounds.setY(transformComponent.pos.y);
        boundsComponent.bounds.setWidth(textureComponent.getFrameWidth());
        boundsComponent.bounds.setHeight(textureComponent.getFrameHeight());

        attachmentComponent.offset.y = 10;
        attachmentComponent.setParentEntity(player);

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

