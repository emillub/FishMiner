package com.github.FishMiner.domain.ecs.util;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.FishComponent;
import com.github.FishMiner.domain.ecs.components.PositionComponent;


public final class HookUtil {

    private HookUtil() {}

    /**
     * Returns the hook entity, if any.
     * The hook is defined as an entity with a BoundsComponent and PositionComponent
     * but without a FishComponent.
     *
     * @param engine the Ashley engine.
     * @return the hook entity, or null if none exist.
     */
    public static Entity getHook(Engine engine) {
        ImmutableArray<Entity> hookEntities = engine.getEntitiesFor(
            Family.all(BoundsComponent.class, PositionComponent.class)
                .exclude(FishComponent.class)
                .get()
        );
        return hookEntities.size() > 0 ? hookEntities.first() : null;
    }
}
