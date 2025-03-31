package com.github.FishMiner.domain.ecs.entityFactories.playerFactory;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.RotationComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.TextureComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;
import com.github.FishMiner.domain.ecs.entityFactories.IEntityFactory;
import com.github.FishMiner.domain.states.HookStates;


/**
 * This is not used any longer. Only needed for testing, so do not use it otherwise
 * @PlayerFactory replaces this class
 */
public class HookFactory implements IEntityFactory {

    private final Engine engine;

    public HookFactory(Engine engine) {
        this.engine = engine;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Entity createEntity(int x, int y) {
        Entity hook = new Entity();

        HookComponent hookComponent = engine.createComponent(HookComponent.class);
        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
        RotationComponent rotationComponent = engine.createComponent(RotationComponent.class);
        BoundsComponent boundsComponent = engine.createComponent(BoundsComponent.class);
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);
        VelocityComponent velocityComponent = engine.createComponent(VelocityComponent.class);
        StateComponent<HookStates> stateComponent = engine.createComponent(StateComponent.class);


        transformComponent.pos.x = x;
        transformComponent.pos.y = y;

        // rotation
        hook.add(rotationComponent);

        stateComponent.changeState(HookStates.SWINGING);

        textureComponent.setRegion("hook_1cols_1rows.png");
        System.out.println(textureComponent.texturePath);

        // TODO: fix misplaced bounds
        boundsComponent.bounds.setX(transformComponent.pos.x);
        boundsComponent.bounds.setY(transformComponent.pos.y);
        boundsComponent.bounds.setWidth(textureComponent.getFrameWidth());
        boundsComponent.bounds.setHeight(textureComponent.getFrameHeight());


        hook.add(hookComponent);
        hook.add(transformComponent);
        hook.add(velocityComponent);
        hook.add(boundsComponent);
        hook.add(rotationComponent);
        hook.add(stateComponent);
        hook.add(textureComponent);

        return hook;
    }
}
