package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.FishComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;
import com.github.FishMiner.domain.events.impl.FishHitEvent;
import com.github.FishMiner.domain.events.GameEventBus;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.ashley.core.EntitySystem;
import com.github.FishMiner.domain.ecs.components.HookComponent;


public class CollisionSystem extends EntitySystem {

    // A set to track fish that have already triggered a hit event.
    private final Set<Entity> caughtFish = new HashSet<>();

    private final Family fishFamily = Family.all(
        BoundsComponent.class,
        FishComponent.class,
        TransformComponent.class,
        StateComponent.class,
        VelocityComponent.class
    ).get();

    private final Family hookFamily = Family.all(
        BoundsComponent.class,
        HookComponent.class,
        TransformComponent.class
    ).get();

    @Override
    public void update(float deltaTime) {
        Engine engine = getEngine();
        ImmutableArray<Entity> fishEntities = engine.getEntitiesFor(fishFamily);
        ImmutableArray<Entity> hookEntities = engine.getEntitiesFor(hookFamily);

        // Loop through every hook entity.
        for (Entity hook : hookEntities) {
            BoundsComponent hookBounds = hook.getComponent(BoundsComponent.class);

            // For each hook, check every fish.
            for (Entity fish : fishEntities) {
                if (caughtFish.contains(fish)) {
                    continue; // Skip fish that have already been processed.
                }
                BoundsComponent fishBounds = fish.getComponent(BoundsComponent.class);

                // If the hook's bounds overlap the fish's bounds…
                if (hookBounds.bounds.overlaps(fishBounds.bounds)) {
                    // Post a FishHitEvent for the fish.
                    FishHitEvent event = new FishHitEvent(fish);
                    //event.setSource(hook);
                    GameEventBus.getInstance().post(event);

                    // Mark this fish as "caught" so we don't process it again.
                    caughtFish.add(fish);
                }
            }
        }
    }
}
