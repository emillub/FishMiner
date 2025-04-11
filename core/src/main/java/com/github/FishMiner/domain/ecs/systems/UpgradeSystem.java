package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.UpgradeComponent;

public class UpgradeSystem extends IteratingSystem {
    ComponentMapper<UpgradeComponent> upgradeMapper = ComponentMapper.getFor(UpgradeComponent.class);
    ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);

    public UpgradeSystem() {
        super(Family.all(UpgradeComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        UpgradeComponent upgrade = upgradeMapper.get(entity);
        if (upgrade.isUpgraded()) {
            entity.remove(upgrade.getClass());
        }
    }
}
