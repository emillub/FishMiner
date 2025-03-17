package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.FishComponent;
import com.github.FishMiner.domain.ecs.components.PositionComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.util.ValidateUtil;
import com.github.FishMiner.domain.events.IGameEvent;
import com.github.FishMiner.domain.events.impl.FishHitEvent;
import com.github.FishMiner.domain.events.GameEventBus;
import com.github.FishMiner.domain.states.FishableObjectStates;
import com.github.FishMiner.domain.states.HookStates;


import java.util.HashSet;
import java.util.Set;

public class CollisionSystem extends IteratingSystem {

    private final ComponentMapper<FishComponent> fm = ComponentMapper.getFor(FishComponent.class);
    private final ComponentMapper<BoundsComponent> bm = ComponentMapper.getFor(BoundsComponent.class);

    private Entity hook;
    private final Set<Entity> caughtFish = new HashSet<>();

    public CollisionSystem() {
        super(Family.all(BoundsComponent.class, FishComponent.class).get());
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        assignHook(engine);
    }

    private void assignHook(Engine engine) {
        ImmutableArray<Entity> hookEntities = engine.getEntitiesFor(
            Family.all(BoundsComponent.class, PositionComponent.class)
                .exclude(FishComponent.class)
                .get()
        );

        if (hookEntities.size() > 0) {
            hook = hookEntities.first();
        }
    }

    @Override
    protected void processEntity(Entity fish, float deltaTime) {
        if (hook == null) {
            assignHook(getEngine());
            if (hook == null) return;
        }

        if (caughtFish.contains(fish)) {
            return;
        }

        StateComponent fishState = fish.getComponent(StateComponent.class);
        StateComponent hookState = hook.getComponent(StateComponent.class);

        ValidateUtil.validateNotNull(fishState, hookState);
        // exit if hook is not in Fire state or the thing that is hit is not fishable
        if (hookState.state != HookStates.FIRE || fishState.state == FishableObjectStates.FISHABLE) {
            return;
        }

        BoundsComponent fishBounds = bm.get(fish);
        BoundsComponent hookBounds = bm.get(hook);

        if (fishBounds.bounds.overlaps(hookBounds.bounds)) {
            handleCollision(fish);
        }
    }
    private void handleCollision(Entity fish) {
        if (caughtFish.add(fish)) {
            System.out.println("🎣 Fish caught! Fish: " + fish);

            // Post an event for the fish
            FishHitEvent fishHitEvent = new FishHitEvent(fish);
            GameEventBus.getInstance().post(fishHitEvent);
        }
    }
}
