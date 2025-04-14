package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.github.FishMiner.common.Logger;
import com.github.FishMiner.domain.ecs.components.AttachmentComponent;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.PlayerComponent;
import com.github.FishMiner.domain.ecs.components.ReelComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.RotationComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;
import com.github.FishMiner.common.ValidateUtil;
import com.github.FishMiner.domain.states.HookStates;


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
    private static final String TAG = "HookSystem";
    private boolean initialized = false;
    private ComponentMapper<HookComponent> hm = ComponentMapper.getFor(HookComponent.class);
    private ComponentMapper<TransformComponent> pm = ComponentMapper.getFor(TransformComponent.class);
    private ComponentMapper<RotationComponent> rm = ComponentMapper.getFor(RotationComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<StateComponent> sm = ComponentMapper.getFor(StateComponent.class);
    private ComponentMapper<BoundsComponent> bm = ComponentMapper.getFor(BoundsComponent.class);
    private ComponentMapper<AttachmentComponent> am = ComponentMapper.getFor(AttachmentComponent.class);
    private ComponentMapper<ReelComponent> reelMapper = ComponentMapper.getFor(ReelComponent.class);

    private float initialPosition;
    private float time; // time accumulator to drive the swinging oscillation

    public HookSystem() {
        super(Family.all(HookComponent.class, TransformComponent.class, BoundsComponent.class).get());
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
        TransformComponent hookPos = pm.get(entity);
        RotationComponent hookRot = rm.get(entity);
        VelocityComponent hookVel = vm.get(entity);
        StateComponent hookState = sm.get(entity);
        BoundsComponent hookBounds = bm.get(entity);
        AttachmentComponent hookAttachment = am.get(entity);

        Entity player = hookAttachment.getParent();
        PlayerComponent playerComponent = player.getComponent(PlayerComponent.class);
        Entity reelEntity = playerComponent.getReel();
        ReelComponent reel = (reelEntity != null) ? reelMapper.get(reelEntity) : null;


        if (!initialized) {
            initialPosition = hook.anchorPoint.y - hook.swingOffset;
            initialized = true;
        }

        ValidateUtil.validateMultipleNotNull(
            hookPos, "Hook Position cannot be null",
            hookRot, "Hook rotation cannot be null",
            hookVel, "Hook Velocity cannot be null"
        );


        if (hookState.state == HookStates.SWINGING) {
            // Calculate the swing angle (in radians) using a sine oscillation.
            hook.swingAngle = hook.swingAmplitude * MathUtils.sin(time);

            // Fixed rope length when swinging (offset from the anchor point).
            float offset = 80.0f;

            // Calculate the new hook position relative to the anchor.
            float posX = hook.anchorPoint.x + offset * MathUtils.sin(hook.swingAngle);
            float posY = hook.anchorPoint.y - offset * MathUtils.cos(hook.swingAngle);
            hookPos.pos.set(posX, posY, 1);

            // Calculate the vector from the anchor to the hook.
            float dx = posX - hook.anchorPoint.x;
            float dy = posY - hook.anchorPoint.y;

            hookRot.angle = MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees + 90;

        }
        float lineLength = (reel != null) ? reel.lineLength : 100f;
        float returnSpeed = (reel != null) ? reel.returnSpeed : 100f;

        if (hookState.state == HookStates.FIRE) {
            // Check if the hook has reached or exceeded the reel length OR if a fish is attached.
            if (hookPos.pos.dst(hook.anchorPoint) >= lineLength || hook.attachedFishableEntity != null) {
                hookState.changeState(HookStates.REELING);
                if (hook.hasAttachedEntity()) {
                  Logger.getInstance().log(TAG, "Hook has attached entity: " + hook.attachedFishableEntity.toString());
                }
            } else {
                hookVel.velocity.set(hook.sinkerWeight, hook.sinkerWeight).setAngleDeg(hookRot.angle - 90);
                hookVel.velocity.scl(returnSpeed);
                hookBounds.bounds.setPosition(hookPos.pos.x, hookPos.pos.y);
            }
        }


        if (hookState.state == HookStates.REELING) {
            if (hookPos.pos.y < initialPosition) {
                // Set the velocity to be reversed so the hook returns to its initial position.
                hookVel.velocity.set(hook.sinkerWeight, hook.sinkerWeight).setAngleDeg(hookRot.angle - 90).scl(-returnSpeed);

                // Optionally, multiply by a speed factor if needed.
            } else {
                hookState.changeState(HookStates.RETURNED);
            }
        }

        if (hookState.state == HookStates.RETURNED) {
            if (!hook.hasAttachedEntity()) {
                hookState.changeState(HookStates.SWINGING);
            }

        }
        Logger.getInstance().debug(TAG, "Line length: " + lineLength + ", Return speed: " + returnSpeed);

    }
}
