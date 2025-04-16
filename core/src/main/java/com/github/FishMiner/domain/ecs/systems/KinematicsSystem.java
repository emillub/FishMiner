package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;

public class KinematicsSystem extends IteratingSystem {
    private final ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);

    public KinematicsSystem() {
        super(Family.all(TransformComponent.class, VelocityComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent trans = tm.get(entity);
        VelocityComponent vel = vm.get(entity);
        Vector3 velCopy = new Vector3(vel.velocity.x, vel.velocity.y, trans.pos.z);
        // Simply apply the velocity update.
        trans.pos.add(velCopy.scl(deltaTime));
    }
}
