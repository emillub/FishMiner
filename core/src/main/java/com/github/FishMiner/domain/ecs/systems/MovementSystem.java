package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.PositionComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;


public class MovementSystem extends IteratingSystem {

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);

    public MovementSystem() {
        super(Family.all(PositionComponent.class, VelocityComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = pm.get(entity);
        VelocityComponent velocity = vm.get(entity);
        BoundsComponent bounds = entity.getComponent(BoundsComponent.class);

        if (position != null && velocity != null) {
            position.position.add(velocity.velocity.cpy().scl(deltaTime));

            if (bounds == null) {
                System.out.println("Warning: BoundsComponent is missing for " + entity);
            } else {
                bounds.bounds.setPosition(position.position.x, position.position.y);
            }}
        else{
              System.out.println("Position or Velocity == null");
        }

    }
}
