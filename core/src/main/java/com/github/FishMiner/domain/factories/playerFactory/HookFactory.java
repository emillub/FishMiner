package com.github.FishMiner.domain.factories.playerFactory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.github.FishMiner.domain.ecs.components.AttachmentComponent;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.RotationComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.TextureComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;
import com.github.FishMiner.domain.ecs.components.WeightComponent;
import com.github.FishMiner.domain.states.FishingRodState;

/**
 * This is not used any longer. Only needed for testing, so do not use it otherwise.
 * @PlayerFactory replaces this class.
 */
public class HookFactory {

    private HookFactory() {

    }

    protected static Entity createEntity(PooledEngine engine, int posZ, Vector3 anchorPoint) {
        Entity hook = engine.createEntity();

        HookComponent hookComponent = engine.createComponent(HookComponent.class);
        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
        RotationComponent rotationComponent = engine.createComponent(RotationComponent.class);
        BoundsComponent boundsComponent = engine.createComponent(BoundsComponent.class);
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);
        VelocityComponent velocityComponent = engine.createComponent(VelocityComponent.class);
        StateComponent<FishingRodState> stateComponent = engine.createComponent(StateComponent.class);
        AttachmentComponent attachmentComponent = engine.createComponent(AttachmentComponent.class);
        WeightComponent weightComponent = engine.createComponent(WeightComponent.class);

        textureComponent.setRegion("hook_1cols_1rows.png");
        stateComponent.changeState(FishingRodState.SWINGING);
        weightComponent.weight = 100f;
        velocityComponent.velocity = new Vector2(0, 0);

        hookComponent.anchorPoint.set(anchorPoint);
        transformComponent.pos.z = posZ + 1;

        boundsComponent.bounds.setPosition(
            transformComponent.pos.x - boundsComponent.bounds.width * 0.5f,
            transformComponent.pos.y - boundsComponent.bounds.height * 0.5f
        );

        boundsComponent.bounds.setSize(
            textureComponent.getFrameWidth(),
            textureComponent.getFrameHeight()
        );

        hook.add(textureComponent);
        hook.add(weightComponent);
        hook.add(hookComponent);
        hook.add(transformComponent);
        hook.add(rotationComponent);
        hook.add(velocityComponent);
        hook.add(stateComponent);
        hook.add(attachmentComponent);
        hook.add(boundsComponent);

        return hook;
    }
}
