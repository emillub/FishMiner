package com.github.FishMiner.domain.ecs.systems;


import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.states.FishableObjectStates;


public class RotationSystem extends IteratingSystem {
    private ComponentMapper<TransformComponent> pm = ComponentMapper.getFor(TransformComponent.class);
    private ComponentMapper<HookComponent> hm = ComponentMapper.getFor(HookComponent.class);
    private ComponentMapper<StateComponent> sm = ComponentMapper.getFor(StateComponent.class);

    public RotationSystem() {
        super(Family.all(TransformComponent.class, HookComponent.class, StateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent hookTrans = pm.get(entity);
        HookComponent hook = hm.get(entity);
        StateComponent state = sm.get(entity);



        if (hookTrans != null && hook != null && state != null) {
            if (hook.hasAttachedEntity()) {
                TransformComponent attachEntityPos = pm.get(hook.attachedFishableEntity);
                StateComponent fishState = sm.get(hook.attachedFishableEntity);
                if (attachEntityPos == null || fishState == null) {
                    return;
                }
                float targetAngle = getRotationAngle(hook.anchorPoint, attachEntityPos.pos);
                if (fishState.getState().equals(FishableObjectStates.HOOKED)) {
                    attachEntityPos.rotation = MathUtils.lerpAngleDeg(
                        attachEntityPos.rotation,
                        targetAngle,
                            2f * deltaTime
                    );

                }
                if (fishState.getState().equals(FishableObjectStates.CAPTURED)) {
                    attachEntityPos.rotation = attachEntityPos.rotation * (45f * deltaTime);
                }
            }
        }
    }

    private float getRotationAngle(Vector3 hookPos, Vector3 fishPos) {
        float angleRad = MathUtils.atan2(hookPos.y - fishPos.y, hookPos.x - fishPos.x);
        return MathUtils.radiansToDegrees * angleRad;
    }
}
