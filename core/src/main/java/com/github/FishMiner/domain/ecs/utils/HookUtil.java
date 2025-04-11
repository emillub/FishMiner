package com.github.FishMiner.domain.ecs.utils;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.FishableComponent;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;


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
            Family.all(HookComponent.class, BoundsComponent.class, TransformComponent.class, StateComponent.class)
                .exclude(FishableComponent.class)
                .get()
        );
        Entity hook = hookEntities.size() > 0 ? hookEntities.first() : null;
        if (hook == null) {
            throw new RuntimeException("HookUtil says: Hook cannot be null");
        }
        return hook;
    }
}
