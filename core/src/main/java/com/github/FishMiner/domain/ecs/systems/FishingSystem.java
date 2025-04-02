package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.github.FishMiner.Logger;
import com.github.FishMiner.domain.ecs.components.FishComponent;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.RotationComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.events.impl.FishCapturedEvent;
import com.github.FishMiner.domain.events.impl.FishHitEvent;
import com.github.FishMiner.domain.events.GameEventBus;
import com.github.FishMiner.domain.listeners.IGameEventListener;
import com.github.FishMiner.domain.states.FishableObjectStates;
import com.github.FishMiner.domain.states.HookStates;

public class FishingSystem extends EntitySystem implements IGameEventListener<FishHitEvent> {
    public static final String TAG = "FishingSystem";
    private ComponentMapper<HookComponent> hookMapper = ComponentMapper.getFor(HookComponent.class);
    private ComponentMapper<TransformComponent> posMapper = ComponentMapper.getFor(TransformComponent.class);
    private ComponentMapper<RotationComponent> rotMapper = ComponentMapper.getFor(RotationComponent.class);
    private ComponentMapper<StateComponent> stateMapper = ComponentMapper.getFor(StateComponent.class);
    private ComponentMapper<FishComponent> fishMapper = ComponentMapper.getFor(FishComponent.class);

    private Entity hookEntity;

    /**
     * must be added to GameEventBus
     */
    public FishingSystem() {
    }

    @Override
    public void addedToEngine(Engine engine) {
        ImmutableArray<Entity> hooks = engine.getEntitiesFor(
            Family.all(HookComponent.class, TransformComponent.class).get()
        );
        if (hooks.size() > 0) {
            hookEntity = hooks.first();
        }
    }

    @Override
    public void update(float deltaTime) {
        if (hookEntity == null) return;

        HookComponent hook = hookMapper.get(hookEntity);
        StateComponent<HookStates> hookState = hookEntity.getComponent(StateComponent.class);

        // Process the fish attached to the hook, if any.
        if (hook.hasAttachedEntity()) {
            Entity fishEntity = hook.attachedFishableEntity;
            TransformComponent fishPos = posMapper.get(fishEntity);
            RotationComponent fishRot = rotMapper.get(fishEntity);
            StateComponent<FishableObjectStates> fishState = fishEntity.getComponent(StateComponent.class);
            FishComponent fishComp = fishEntity.getComponent(FishComponent.class);

            // 1. If the hook is in FIRE state and the fish is FISHABLE, then hook it.
            if (hookState.state == HookStates.FIRE && fishState.state == FishableObjectStates.FISHABLE) {
                fishState.changeState(FishableObjectStates.HOOKED);
                hookState.changeState(HookStates.REELING);
            }

            // 2. If the hook is REELING, update the fish's position relative to the hook.
            if (hookState.state == HookStates.REELING) {
                Vector3 rotatedOffset = new Vector3(hook.offset)
                    .rotate(new Vector3(0, 0, 1), hook.swingAngle * MathUtils.radiansToDegrees);
                TransformComponent hookPos = posMapper.get(hookEntity);
                fishPos.pos.set(hookPos.pos).add(rotatedOffset);

                if (fishRot != null) {
                    fishRot.angle = hook.swingAngle * MathUtils.radiansToDegrees;
                }
            }

            // 3. When the hook is RETURNED, determine the outcome.
            if (hookState.state == HookStates.RETURNED) {
                if (fishComp != null && fishComp.getValue() > 0) {
                    fishState.changeState(FishableObjectStates.CAPTURED);
                    GameEventBus.getInstance().post(new FishCapturedEvent(fishEntity));
                } else {
                    fishState.changeState(FishableObjectStates.ATTACKING);
                }
                // Detach the fish and reset the hook.
                hook.attachedFishableEntity = null;
                hookState.changeState(HookStates.SWINGING);
            }
        }
    }

    @Override
    public void onEvent(FishHitEvent event) {
        if (event.isHandled()) {
            return;
        }
        if (hookEntity == null) {
            ImmutableArray<Entity> hooks = getEngine().getEntitiesFor(
                Family.all(HookComponent.class, TransformComponent.class).get()
            );
            if (hooks.size() > 0) {
                hookEntity = hooks.first();
            }
        }

        // When a FishHitEvent is received, attach the fish to the hook if none is attached.
        if (hookEntity != null) {
            HookComponent hook = hookMapper.get(hookEntity);
            if (hook.attachedFishableEntity == null) {
                hook.attachedFishableEntity = event.getTarget();
                event.setHandled();
                Logger.getInstance().log(TAG, "Fish hit processed: attached fish " + event.getTarget());
                System.out.println();
            }
        }
    }

    @Override
    public Class<FishHitEvent> getEventType() {
        return FishHitEvent.class;
    }
}
