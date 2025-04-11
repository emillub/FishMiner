package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.common.ValidateUtil;

public class PhysicalSystem extends IteratingSystem {
    private final ComponentMapper<TransformComponent> pm = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<BoundsComponent> bm = ComponentMapper.getFor(BoundsComponent.class);

    public PhysicalSystem() {
        super(Family.all(TransformComponent.class, BoundsComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent position = pm.get(entity);
        BoundsComponent bounds = bm.get(entity);

        try {
            ValidateUtil.validateMultipleNotNull(position, bounds);
            bounds.bounds.x = position.pos.x - bounds.bounds.width * 0.5f;
            bounds.bounds.y = position.pos.y - bounds.bounds.height * 0.5f;
            //bounds.bounds.setX(position.pos.x);
            //bounds.bounds.setY(position.pos.y);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Physical Entity is missing component:" + e.getLocalizedMessage());
        }

    }

}
