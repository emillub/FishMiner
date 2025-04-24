package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.github.FishMiner.infrastructure.Logger;
import com.github.FishMiner.infrastructure.GameEventBus;
import com.github.FishMiner.domain.ecs.components.*;
import com.github.FishMiner.domain.ecs.events.FishCapturedEvent;
import com.github.FishMiner.domain.ecs.events.FishHitEvent;
import com.github.FishMiner.infrastructure.ports.in.IGameEventListener;
import com.github.FishMiner.domain.ecs.states.FishableObjectStates;
import com.github.FishMiner.domain.ecs.states.HookStates;

public class FishingSystem extends EntitySystem implements IGameEventListener<FishHitEvent> {
    public static final String TAG = "FishingSystem";
    private final Vector3 rotatedOffset = new Vector3();

    private final ComponentMapper<HookComponent> hookMapper = ComponentMapper.getFor(HookComponent.class);
    private final ComponentMapper<TransformComponent> posMapper = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<RotationComponent> rotMapper = ComponentMapper.getFor(RotationComponent.class);
    private final ComponentMapper<StateComponent> stateMapper = ComponentMapper.getFor(StateComponent.class);
    private final ComponentMapper<FishableComponent> fishMapper = ComponentMapper.getFor(FishableComponent.class);
    private final ComponentMapper<AttachmentComponent> attachmentMapper = ComponentMapper.getFor(AttachmentComponent.class);

    public FishingSystem() {
        // Constructor remains empty
    }

    @Override
    public void update(float deltaTime) {
        // Always get the latest hook entity in case of game reset
        ImmutableArray<Entity> hooks = getEngine().getEntitiesFor(
            Family.all(HookComponent.class, TransformComponent.class, AttachmentComponent.class).get()
        );

        if (hooks.size() == 0) return;

        Entity hookEntity = hooks.first();
        HookComponent hook = hookMapper.get(hookEntity);

        if (hook == null || !hook.hasAttachedEntity()) return;

        StateComponent<HookStates> hookState = stateMapper.get(hookEntity);
        TransformComponent hookPos = posMapper.get(hookEntity);
        AttachmentComponent hookParent = attachmentMapper.get(hookEntity);

        Entity fishEntity = hook.attachedFishableEntity;
        if (fishEntity == null) return;

        StateComponent<FishableObjectStates> fishState = stateMapper.get(fishEntity);
        TransformComponent fishPos = posMapper.get(fishEntity);
        RotationComponent fishRot = rotMapper.get(fishEntity);
        FishableComponent fishComp = fishMapper.get(fishEntity);

        fishState.changeState(FishableObjectStates.HOOKED);

        if (hookState.state == HookStates.REELING) {
            rotatedOffset.set(hook.offset)
                    .rotate(new Vector3(0, 0, 1), hook.swingAngle * MathUtils.radiansToDegrees);
            fishPos.pos.set(hookPos.pos).add(rotatedOffset);

            fishPos.pos.set(hookPos.pos).add(rotatedOffset);

            if (fishRot != null) {
                fishRot.angle = hook.swingAngle * MathUtils.radiansToDegrees;
            }
        }

        if (hookState.state == HookStates.RETURNED) {
            if (fishComp != null) {
                fishState.changeState(FishableObjectStates.CAPTURED);
                Logger.getInstance().debug(TAG, "FishState was changed to CAPTURED");
                Entity playerParent = hookParent.getParent();
                GameEventBus.getInstance().post(new FishCapturedEvent(fishEntity, playerParent));
                fishEntity.remove(VelocityComponent.class); // stop movement
            } else {
                fishState.changeState(FishableObjectStates.ATTACKING);
            }
            hook.attachedFishableEntity = null;
        }
    }

    @Override
    public void onEvent(FishHitEvent event) {
        Logger.getInstance().log(TAG, "FishHitEvent arrived");

        if (event.isHandled()) return;

        ImmutableArray<Entity> hooks = getEngine().getEntitiesFor(
            Family.all(HookComponent.class, TransformComponent.class, AttachmentComponent.class).get()
        );
        if (hooks.size() == 0) return;

        Entity hookEntity = hooks.first();
        HookComponent hook = hookMapper.get(hookEntity);

        if (hook.attachedFishableEntity == null) {
            hook.attachedFishableEntity = event.getTarget();
            StateComponent<FishableObjectStates> fishState = stateMapper.get(event.getTarget());
            fishState.changeState(FishableObjectStates.HOOKED);
            event.setHandled();
            Logger.getInstance().log(TAG, "Fish hit processed: attached fish " + event.getTarget());
        }
    }

    @Override
    public Class<FishHitEvent> getEventType() {
        return FishHitEvent.class;
    }
}
