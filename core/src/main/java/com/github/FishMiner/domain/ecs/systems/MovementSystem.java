package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;
import com.github.FishMiner.domain.states.FishableObjectStates;


public class MovementSystem extends IteratingSystem {

    private ComponentMapper<TransformComponent> pm = ComponentMapper.getFor(TransformComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<StateComponent> sm = ComponentMapper.getFor(StateComponent.class);

    public MovementSystem() {
        super(Family.all(TransformComponent.class, VelocityComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent position = pm.get(entity);
        VelocityComponent velocity = vm.get(entity);

        if (position != null && velocity != null) {
            position.pos.x += velocity.velocity.x * deltaTime;
            position.pos.y += velocity.velocity.y * deltaTime;
        }

        StateComponent fishState = sm.get(entity);
        if (fishState != null && position != null && fishState.getState().equals(FishableObjectStates.FISHABLE)) {
            position.rotation = 0f;
        }
    }
}
