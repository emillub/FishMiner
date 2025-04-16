package com.github.FishMiner.domain.ecs.utils;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.FishableComponent;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.SinkerComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.WeightComponent;


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


    /**
     * Calculates the fire speed for the hook.
     */
    public static float calculateFireSpeed(float hookBaseSpeed, SinkerComponent sinkerComp) {
        // If a sinker is installed, use its weight as a bonus; otherwise, default bonus is 20f.
        float bonusSpeed = (sinkerComp != null) ? sinkerComp.weight : 20f;
        return hookBaseSpeed + bonusSpeed;
    }

    /**
     * Calculates the return speed by reducing the base return speed by the weight of the fish,
     * ensuring a minimum return speed.
     */
    public static float calculateReturnSpeed(float baseReturnSpeed, WeightComponent fishWeightComp) {
        // The minimum return speed is set to 20f.
        return Math.max(baseReturnSpeed - fishWeightComp.weight, 20f);
    }


}
