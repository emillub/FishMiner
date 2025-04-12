package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.FishableComponent;
import com.github.FishMiner.domain.ecs.components.RotationComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;
import com.github.FishMiner.domain.events.ecsEvents.FishHitEvent;
import com.github.FishMiner.domain.GameEventBus;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.ashley.core.EntitySystem;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.states.FishableObjectStates;
import com.github.FishMiner.domain.states.HookStates;


public class CollisionSystem extends EntitySystem {

    Engine engine;
    private ComponentMapper<HookComponent> hookMapper = ComponentMapper.getFor(HookComponent.class);
    private ComponentMapper<TransformComponent> posMapper = ComponentMapper.getFor(TransformComponent.class);
    private ComponentMapper<RotationComponent> rotMapper = ComponentMapper.getFor(RotationComponent.class);
    private ComponentMapper<StateComponent> stateMapper = ComponentMapper.getFor(StateComponent.class);
    private ComponentMapper<FishableComponent> fishMapper = ComponentMapper.getFor(FishableComponent.class);

    private final Set<Entity> caughtFish = new HashSet<>(); // A set to track fish that have already triggered a hit event.
    private Entity hookEntity;

    private final Family fishFamily = Family.all(
        BoundsComponent.class,
        FishableComponent.class,
        TransformComponent.class,
        StateComponent.class,
        VelocityComponent.class
    ).get();

    private final Family hookFamily = Family.all(
        HookComponent.class,
        BoundsComponent.class,
        TransformComponent.class
    ).get();

    @Override
    public void addedToEngine(Engine engine) {
        this.engine = engine;
        ImmutableArray<Entity> hooks = engine.getEntitiesFor(hookFamily);
        if (hooks.size() > 0) {
            hookEntity = hooks.first();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void update(float deltaTime) {
        if (hookEntity == null) {
            ImmutableArray<Entity> hooks = getEngine().getEntitiesFor(hookFamily);
            if (hooks.size() > 0) {
                hookEntity = hooks.first();
            } else {
                throw new IllegalStateException("Hook cannot be null");
            }
        }
        HookComponent hook = hookMapper.get(hookEntity);
        StateComponent<HookStates> hookState = hookEntity.getComponent(StateComponent.class);
        TransformComponent hookPos = posMapper.get(hookEntity);
        BoundsComponent hookBounds = hookEntity.getComponent(BoundsComponent.class);

        // --- Collision Detection ---
        // If the hook is in FIRE state and no fish is attached, check for collisions.
        if (hookState.state == HookStates.FIRE && hook.attachedFishableEntity == null) {
            // Get all current fish entities; this will include dynamically spawned ones.
            ImmutableArray<Entity> fishEntities = getEngine().getEntitiesFor(fishFamily);
            for (Entity fish : fishEntities) {
                StateComponent<FishableObjectStates> fishState = fish.getComponent(StateComponent.class);
                // Only consider fish that are available to be hooked.
                if (fishState.getState() != FishableObjectStates.FISHABLE) {
                    continue;
                }
                BoundsComponent fishBounds = fish.getComponent(BoundsComponent.class);
                // Use your helper method for collision detection.
                if (hookBounds.overlaps(fishBounds)) {
                    System.out.println("HOOK OVERLAPS FISH! Attaching fish " + fish);
                    // Attach the fish to the hook and update states.
                    hook.attachedFishableEntity = fish;
                    fishState.changeState(FishableObjectStates.HOOKED);
                    hookState.changeState(HookStates.REELING);
                    // Optionally post an event.
                    FishHitEvent event = new FishHitEvent(fish);
                    event.setSource(hookEntity);
                    GameEventBus.getInstance().post(event);
                    break; // attach only one fish at a time
                }
            }
        }

    }
}
