package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.PositionComponent;
import com.github.FishMiner.domain.ecs.components.RotationComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;
import com.github.FishMiner.domain.ecs.util.ValidateUtil;
import com.github.FishMiner.domain.events.impl.FishHitEvent;
import com.github.FishMiner.domain.listeners.IGameEventListener;
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
public class HookSystem extends IteratingSystem{
    private ComponentMapper<HookComponent> hm = ComponentMapper.getFor(HookComponent.class);
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<RotationComponent> rm = ComponentMapper.getFor(RotationComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<StateComponent> sm = ComponentMapper.getFor(StateComponent.class);
    private ComponentMapper<BoundsComponent> bm = ComponentMapper.getFor(BoundsComponent.class);

    private float initialPosition;
    private float time; // time accumulator to drive the swinging oscillation

    public HookSystem() {
        super(Family.all(HookComponent.class, PositionComponent.class, BoundsComponent.class).get());
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
        BoundsComponent hookBounds = bm.get(entity);

        if (initialPosition == 0) {
            initialPosition = hook.anchorPoint.y - hook.swingOffset;
        }


        ValidateUtil.validateNotNull(
            hookPos, "Hook Position cannot be null",
            hookRot, "Hook rotation cannot be null",
            hookVel, "Hook Velocity cannot be null"
        );


        if (hookState.state == HookStates.SWINGING) {
            System.out.println("swinging");
            // Calculate the swing angle (in radians) using a sine oscillation.
            hook.swingAngle = hook.swingAmplitude * MathUtils.sin(time);

            // Fixed rope length when swinging (offset from the anchor point).
            float offset = 80.0f;

            // Calculate the new hook position relative to the anchor.
            float posX = hook.anchorPoint.x + offset * MathUtils.sin(hook.swingAngle);
            float posY = hook.anchorPoint.y - offset * MathUtils.cos(hook.swingAngle);
            hookPos.position.set(posX, posY);

            // Calculate the vector from the anchor to the hook.
            float dx = posX - hook.anchorPoint.x;
            float dy = posY - hook.anchorPoint.y;

            // Compute the angle (in degrees) of that vector.
            // MathUtils.atan2 returns the angle relative to the positive x-axis.
            // Adding 90° makes 0° represent the hook hanging straight down.
            hookRot.angle = MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees + 90;

            // Debug output
            System.out.println("Anchor: " + hook.anchorPoint);
            System.out.println("Computed Position: (" + posX + ", " + posY + ")");
            System.out.println("Rotation Angle: " + hookRot.angle);
        }


        if (hookState.state == HookStates.FIRE) {

            // Check if the hook has reached or exceeded the reel length OR if a fish is attached.
            if (hookPos.position.dst(hook.anchorPoint) >= hook.reelLength || hook.hasAttachedEntity()) {
                hookState.changeState(HookStates.REELING);
                if (hook.hasAttachedEntity()) {
                  System.out.println("Returning: Fish caught");
                }
            } else {
                System.out.println("firing");
                hookVel.velocity.set(hook.sinkerWeight, hook.sinkerWeight).setAngleDeg(hookRot.angle - 90);
                hookVel.velocity.scl(100.0f);
                hookBounds.bounds.setPosition(hookPos.position.x, hookPos.position.y);
            }
        }


        if (hookState.state == HookStates.REELING) {

            System.out.println("reeling");
            if (hookPos.position.y < initialPosition) {
                // Set the velocity to be reversed so the hook returns to its initial position.
                hookVel.velocity.set(hook.sinkerWeight, hook.sinkerWeight).setAngleDeg(hookRot.angle - 90).scl(-100.0f);

                // Optionally, multiply by a speed factor if needed.
            } else {
                hookState.changeState(HookStates.RETURNED);
            }
            //if (hookPos.position == hook.anchorPoint) {
            //    hookState.changeState(HookStates.RETURNED);
            //}

        }

        if (hookState.state == HookStates.RETURNED) {
            System.out.println("returned");
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
