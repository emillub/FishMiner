package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.PositionComponent;
import com.github.FishMiner.domain.ecs.components.TextureComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;
import com.github.FishMiner.domain.ecs.util.ValidateUtil;

public class PhysicalSystem extends IteratingSystem {
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<BoundsComponent> bm = ComponentMapper.getFor(BoundsComponent.class);

    public PhysicalSystem() {
        super(Family.all(PositionComponent.class, BoundsComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = pm.get(entity);
        BoundsComponent bounds = bm.get(entity);

        try {
            ValidateUtil.validateNotNull(position, bounds);
            bounds.bounds.setPosition(position.position);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Physical Entity is missing component:" + e.getLocalizedMessage());
        }

    }

}
