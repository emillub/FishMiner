package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.github.FishMiner.domain.ecs.components.FishComponent;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.PositionComponent;
import com.github.FishMiner.domain.ecs.components.RotationComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.events.GameEventBus;
import com.github.FishMiner.domain.events.impl.FishCapturedEvent;
//import com.github.FishMiner.domain.events.impl.SharkAttackingEvent;
import com.github.FishMiner.domain.states.FishableObjectStates;
import com.github.FishMiner.domain.states.HookStates;

public class FishingSystem extends IteratingSystem {

    private ComponentMapper<HookComponent> hookMapper = ComponentMapper.getFor(HookComponent.class);
    private ComponentMapper<PositionComponent> posMapper = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<RotationComponent> rotMapper = ComponentMapper.getFor(RotationComponent.class);

    public FishingSystem() {
        // We iterate over hooks, as the hook holds the reference to an attached fishable entity.
        super(Family.all(HookComponent.class, PositionComponent.class, StateComponent.class).get());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void processEntity(Entity hookEntity, float deltaTime) {
        HookComponent hook = hookMapper.get(hookEntity);
        PositionComponent hookPos = posMapper.get(hookEntity);
        StateComponent<HookStates> hookState = (StateComponent<HookStates>) hookEntity.getComponent(StateComponent.class);

        // Proceed only if a fishable entity (fish/shark) is attached to the hook.
        if (hook.attachedFishableEntity != null) {
            Entity fishEntity = hook.attachedFishableEntity;
            PositionComponent fishPos = posMapper.get(fishEntity);
            RotationComponent fishRot = rotMapper.get(fishEntity);
            StateComponent<FishableObjectStates> fishState = (StateComponent<FishableObjectStates>) fishEntity.getComponent(StateComponent.class);
            FishComponent fishComp = fishEntity.getComponent(FishComponent.class);

            // 1. When the hook is FIRE and the fishable is FISHABLE,
            // change the fishable state to HOOKED and update the hook to REELING.
            if (hookState.state == HookStates.FIRE && fishState.state == FishableObjectStates.FISHABLE) {
                fishState.changeState(FishableObjectStates.HOOKED);
                hookState.changeState(HookStates.REELING);
            }

            // 2. If the hook is REELING, update the fish's position relative to the hook.
            if (hookState.state == HookStates.REELING) {
                Vector2 rotatedOffset = new Vector2(hook.offset).rotateRad(hook.swingAngle);
                fishPos.position.set(hookPos.position).add(rotatedOffset);
                if (fishRot != null) {
                    fishRot.angle = hook.swingAngle * MathUtils.radiansToDegrees;
                }
            }

            // 3. When the hook is RETURNED, decide the outcome based on the fish's value:
            // - Positive value: the fish is captured.
            // - Negative value: it's a shark and begins attacking.
            if (hookState.state == HookStates.RETURNED) {
                if (fishComp != null && fishComp.getValue() > 0) {
                    fishState.changeState(FishableObjectStates.CAPTURED);
                    GameEventBus.getInstance().post(new FishCapturedEvent(fishEntity));
                } else {
                    fishState.changeState(FishableObjectStates.ATTACKING);
                    //GameEventBus.getInstance().post(new SharkAttackingEvent(fishEntity));
                }
                // Detach the fishable entity from the hook and set the hook back to SWINGING.
                hook.attachedFishableEntity = null;
                hookState.changeState(HookStates.SWINGING);
            }
        }
    }
}
