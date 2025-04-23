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
import com.github.FishMiner.domain.ecs.events.FishHitEvent;
import com.github.FishMiner.infrastructure.GameEventBus;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.ashley.core.EntitySystem;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.states.FishableObjectStates;
import com.github.FishMiner.domain.ecs.states.HookStates;

public class CollisionSystem extends EntitySystem {

    private Engine engine;
    private final ComponentMapper<HookComponent> hookMapper = ComponentMapper.getFor(HookComponent.class);
    private final ComponentMapper<TransformComponent> posMapper = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<RotationComponent> rotMapper = ComponentMapper.getFor(RotationComponent.class);
    private final ComponentMapper<StateComponent> stateMapper = ComponentMapper.getFor(StateComponent.class);
    private final ComponentMapper<FishableComponent> fishMapper = ComponentMapper.getFor(FishableComponent.class);

    private final Set<Entity> caughtFish = new HashSet<>(); // A set to track fish that have already triggered a hit event.

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
        TransformComponent.class,
        StateComponent.class
    ).get();

    @Override
    public void addedToEngine(Engine engine) {
        this.engine = engine;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void update(float deltaTime) {
        ImmutableArray<Entity> hooks = engine.getEntitiesFor(hookFamily);
        if (hooks.size() == 0) return;

        Entity hookEntity = hooks.first(); // always get the latest
        HookComponent hook = hookMapper.get(hookEntity);
        StateComponent<HookStates> hookState = stateMapper.get(hookEntity);
        TransformComponent hookPos = posMapper.get(hookEntity);
        BoundsComponent hookBounds = hookEntity.getComponent(BoundsComponent.class);

        if (hook == null || hookState == null || hookBounds == null) return;

        // --- Collision Detection ---
        if (hookState.state == HookStates.FIRE && hook.attachedFishableEntity == null) {
            ImmutableArray<Entity> fishEntities = engine.getEntitiesFor(fishFamily);
            for (Entity fish : fishEntities) {
                StateComponent<FishableObjectStates> fishState = stateMapper.get(fish);
                if (fishState == null || fishState.getState() != FishableObjectStates.FISHABLE) {
                    continue;
                }
                BoundsComponent fishBounds = fish.getComponent(BoundsComponent.class);
                if (fishBounds != null && hookBounds.overlaps(fishBounds)) {
                    TransformComponent fishTransform = fish.getComponent(TransformComponent.class);
                    System.out.println("[DEBUG] HOOK OVERLAPS FISH! " + fish + " at pos " + fishTransform.pos);

                    hook.attachedFishableEntity = fish;
                    fishState.changeState(FishableObjectStates.HOOKED);
                    hookState.changeState(HookStates.REELING);

                    FishHitEvent event = new FishHitEvent(fish);
                    event.setSource(hookEntity);
                    GameEventBus.getInstance().post(event);
                    break;
                }
            }
        }
    }
}
