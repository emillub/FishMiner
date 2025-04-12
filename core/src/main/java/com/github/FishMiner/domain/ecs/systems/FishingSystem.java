package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.github.FishMiner.common.Logger;
import com.github.FishMiner.domain.ecs.components.AttachmentComponent;
import com.github.FishMiner.domain.ecs.components.FishableComponent;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.RotationComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;
import com.github.FishMiner.domain.events.ecsEvents.FishCapturedEvent;
import com.github.FishMiner.domain.events.ecsEvents.FishHitEvent;
import com.github.FishMiner.domain.GameEventBus;
import com.github.FishMiner.domain.ports.in.IGameEventListener;
import com.github.FishMiner.domain.states.FishableObjectStates;
import com.github.FishMiner.domain.states.HookStates;

public class FishingSystem extends EntitySystem implements IGameEventListener<FishHitEvent> {
    public static final String TAG = "FishingSystem";
    private final ComponentMapper<HookComponent> hookMapper = ComponentMapper.getFor(HookComponent.class);
    private final ComponentMapper<TransformComponent> posMapper = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<RotationComponent> rotMapper = ComponentMapper.getFor(RotationComponent.class);
    private final ComponentMapper<StateComponent> stateMapper = ComponentMapper.getFor(StateComponent.class);
    private final ComponentMapper<FishableComponent> fishMapper = ComponentMapper.getFor(FishableComponent.class);
    private final ComponentMapper<AttachmentComponent> attachmentMapper = ComponentMapper.getFor(AttachmentComponent.class);

    private Entity hookEntity;

    /**
     * must be added to GameEventBus
     */
    public FishingSystem() {
    }

    @Override
    public void addedToEngine(Engine engine) {
        ImmutableArray<Entity> hooks = engine.getEntitiesFor(
            Family.all(HookComponent.class, TransformComponent.class, AttachmentComponent.class).get()
        );
        if (hooks.size() > 0) {
            hookEntity = hooks.first();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void update(float deltaTime) {
        if (hookEntity == null) return;

        HookComponent hook = hookMapper.get(hookEntity);

        // Process the fish attached to the hook, if any.
        if (hook.hasAttachedEntity()) {
            StateComponent<HookStates> hookState = hookEntity.getComponent(StateComponent.class);
            TransformComponent hookPos = posMapper.get(hookEntity);
            AttachmentComponent hookParent = attachmentMapper.get(hookEntity);

            Entity fishEntity = hook.attachedFishableEntity;
            StateComponent<FishableObjectStates> fishState = stateMapper.get(fishEntity);
            TransformComponent fishPos = posMapper.get(fishEntity);
            RotationComponent fishRot = rotMapper.get(fishEntity);
            FishableComponent fishComp = fishMapper.get(fishEntity);


            fishState.changeState(FishableObjectStates.HOOKED);


            // 2. If the hook is REELING, update the fish's position relative to the hook.
            if (hookState.state == HookStates.REELING) {
                Vector3 rotatedOffset = new Vector3(hook.offset)
                    .rotate(new Vector3(0, 0, 1), hook.swingAngle * MathUtils.radiansToDegrees);

                fishPos.pos.set(hookPos.pos).add(rotatedOffset);

                if (fishRot != null) {
                    fishRot.angle = hook.swingAngle * MathUtils.radiansToDegrees;
                }
            }

            // 3. When the hook is RETURNED
            if (hookState.state == HookStates.RETURNED) {
                if (fishComp != null) {
                    fishState.changeState(FishableObjectStates.CAPTURED);
                    Logger.getInstance().debug(TAG, "FishState was changed to CAPTURED");
                    Entity playerParent = hookParent.getParent();
                    GameEventBus.getInstance().post(new FishCapturedEvent(fishEntity, playerParent));
                    fishEntity.remove(VelocityComponent.class); // this fish are no longer processed by MovementSystem
                } else {
                    fishState.changeState(FishableObjectStates.ATTACKING);
                }
                hook.attachedFishableEntity = null;
            }
        }
    }

    @Override
    public void onEvent(FishHitEvent event) {
        Logger.getInstance().log(TAG, "FishHitEvent arrived");
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
                StateComponent<FishableObjectStates> fishState = stateMapper.get(event.getTarget());
                fishState.changeState(FishableObjectStates.HOOKED);
                event.setHandled();
                Logger.getInstance().log(TAG, "Fish hit processed: attached fish " + event.getTarget());
            }
        }
    }

    @Override
    public Class<FishHitEvent> getEventType() {
        return FishHitEvent.class;
    }
}
