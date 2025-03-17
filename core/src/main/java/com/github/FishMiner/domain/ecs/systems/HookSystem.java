package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.PositionComponent;
import com.github.FishMiner.domain.ecs.components.RotationComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;
import com.github.FishMiner.domain.ecs.util.ValidateUtil;
import com.github.FishMiner.domain.states.FishableObjectStates;
import com.github.FishMiner.domain.states.HookStates;
import com.github.FishMiner.domain.states.IState;


/**
 * The HookSystem handles the swinging behavior of the hook.
 * It updates the hook’s swing angle using a sine oscillation,
 * applies that to its rotation (if a RotationComponent is present),
 * and if a fish is attached, positions the fish relative to the hook.
 *
 * Note: This system relies on the PositionComponent (and optionally RotationComponent)
 * already updated by your MovementSystem and RotationSystem.
 */
public class HookSystem extends IteratingSystem {
    private ComponentMapper<HookComponent> hm = ComponentMapper.getFor(HookComponent.class);
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<RotationComponent> rm = ComponentMapper.getFor(RotationComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<StateComponent> sm = ComponentMapper.getFor(StateComponent.class);


    private float time; // time accumulator to drive the swinging oscillation

    public HookSystem() {
        super(Family.all(HookComponent.class, PositionComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        time += deltaTime;
        super.update(deltaTime);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void processEntity(Entity entity, float deltaTime) {
        HookComponent hook = hm.get(entity);
        PositionComponent hookPos = pm.get(entity);
        RotationComponent hookRot = rm.get(entity);
        VelocityComponent hookVel = vm.get(entity);
        StateComponent hookState = sm.get(entity);


        ValidateUtil.validateNotNull(
            hookPos, "Hook Position cannot be null",
            hookRot, "Hook rotation cannot be null",
            hookVel, "Hook Velocity cannot be null"
        );

        if (hookState.state == HookStates.SWINGING) {
            // Update the swing angle using a sine oscillation.
            hook.swingAngle = hook.swingAmplitude * MathUtils.sin(time);
            // Update the hook's rotation (convert from radians to degrees).
            hookRot.angle = hook.swingAngle * MathUtils.radiansToDegrees;
        }

        if (hookState.state == HookStates.FIRE) {
            // Set the velocity to move in the direction of the hook's rotation.
            // For example, using a unit vector in that direction.
            hookVel.velocity.set(1, 0).setAngleDeg(hookRot.angle);
            // If you have a speed value, multiply the vector by that speed:
            // hookVel.velocity.scl(hookVel.speed);
        }

        if (hookState.state == HookStates.REELING) {
            // Set the velocity to be reversed so the hook returns to its initial position.
            hookVel.velocity.set(1, 0).setAngleDeg(hookRot.angle).scl(-1);
            // Optionally, multiply by a speed factor if needed.
        }

        if (hookState.state == HookStates.RETURNED) {
            if (hook.hasAttachedEntity()) {
                StateComponent<FishableObjectStates> attachedState = hook.attachedFishableEntity.getComponent(StateComponent.class);
                attachedState.changeState(FishableObjectStates.CAPTURED);
            }
            else {
                hookState.changeState(HookStates.SWINGING);
            }
        }
    }
}
