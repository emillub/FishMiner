package com.github.FishMiner.domain.ecs.systems;


import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.RotationComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.states.FishableObjectStates;


public class RotationSystem extends IteratingSystem {
    private ComponentMapper<TransformComponent> pm = ComponentMapper.getFor(TransformComponent.class);
    private ComponentMapper<RotationComponent> rm = ComponentMapper.getFor(RotationComponent.class);
    private ComponentMapper<StateComponent> sm = ComponentMapper.getFor(StateComponent.class);


    private Vector2 hookPosition = new Vector2();

    public RotationSystem() {
        super(Family.all(TransformComponent.class, RotationComponent.class, StateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent entityPosition = pm.get(entity);
        RotationComponent entityRotation = rm.get(entity);

        StateComponent entityState = sm.get(entity);

        if (entityPosition != null && entityRotation != null && entityState != null) {
            if (entityState.state == FishableObjectStates.HOOKED) {
                float angle = getRotationAngle(hookPosition, entityPosition.pos);
                entityRotation.angle = angle;
            }
        }
    }

    private float getRotationAngle(Vector2 hookPos, Vector3 fishPos) {
        float angleRad = MathUtils.atan2(hookPos.y - fishPos.y, hookPosition.x - fishPos.x);
        return  MathUtils.radiansToDegrees * angleRad;
    }

    public void setHookPosition(float x, float y) {
        hookPosition.set(x, y);
    }

}
