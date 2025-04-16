package com.github.FishMiner.domain.ecs.systems;

import static com.github.FishMiner.domain.states.FishableObjectStates.FISHABLE;
import static com.github.FishMiner.domain.states.FishableObjectStates.HOOKED;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.github.FishMiner.common.ValidateUtil;
import com.github.FishMiner.domain.ecs.components.AttachmentComponent;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.FishingRodComponent;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.ReelComponent;
import com.github.FishMiner.domain.ecs.components.RotationComponent;
import com.github.FishMiner.domain.ecs.components.SinkerComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;
import com.github.FishMiner.domain.ecs.components.WeightComponent;
import com.github.FishMiner.domain.ecs.utils.HookUtil;
import com.github.FishMiner.domain.states.FishableObjectStates;
import com.github.FishMiner.domain.states.FishingRodState;

public class FishingRodSystem extends IteratingSystem {
    private static final String TAG = "FishingRodSystem";

    private boolean initialized = false;
    private float initialPosition;
    private float time;

    // Mappers for the fishing rod entity.
    private final ComponentMapper<FishingRodComponent> fishingrodMapper = ComponentMapper.getFor(FishingRodComponent.class);
    private final ComponentMapper<StateComponent> stateMapper = ComponentMapper.getFor(StateComponent.class);

    // Mappers for the hook entity.
    private final ComponentMapper<HookComponent> hookMapper = ComponentMapper.getFor(HookComponent.class);
    private final ComponentMapper<TransformComponent> transMapper = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<RotationComponent> rotMapper = ComponentMapper.getFor(RotationComponent.class);
    private final ComponentMapper<VelocityComponent> velMapper = ComponentMapper.getFor(VelocityComponent.class);
    private final ComponentMapper<BoundsComponent> bm = ComponentMapper.getFor(BoundsComponent.class);
    private final ComponentMapper<AttachmentComponent> am = ComponentMapper.getFor(AttachmentComponent.class);

    // Mappers for reel and sinker components (retrieved from hook’s corresponding entities).
    private final ComponentMapper<ReelComponent> reelMapper = ComponentMapper.getFor(ReelComponent.class);
    private final ComponentMapper<SinkerComponent> sinkerMapper = ComponentMapper.getFor(SinkerComponent.class);

    private final ComponentMapper<WeightComponent> weightMapper = ComponentMapper.getFor(WeightComponent.class);

    public FishingRodSystem() {
        // The fishing rod system processes entities with a FishingRodComponent.
        super(Family.all(FishingRodComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        time += deltaTime;
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // Retrieve the fishing rod component from the entity.
        FishingRodComponent rod = fishingrodMapper.get(entity);
        // Get the hook, reel, and sinker entities from the rod.
        Entity hookEntity = rod.getHook();
        Entity reelEntity = rod.getReel();
        Entity sinkerEntity = rod.getSinker();

        // Retrieve hook-related components from the hook entity.
        HookComponent hook = hookMapper.get(hookEntity);
        TransformComponent hookPos = transMapper.get(hookEntity);
        RotationComponent hookRot = rotMapper.get(hookEntity);
        VelocityComponent hookVel = velMapper.get(hookEntity);
        AttachmentComponent hookAttachment = am.get(hookEntity);

        // Get the rod’s state.
        StateComponent<FishingRodState> hookState = stateMapper.get(hookEntity);
        ValidateUtil.validateNotNull(hookState, TAG + " -> hookState");

        // Initialize the initial hook position once.
        if (!initialized) {
            initialPosition = hook.anchorPoint.y - hook.swingOffset;
            initialized = true;
        }

        // Validate required components.
        ValidateUtil.validateMultipleNotNull(
            hookPos, "Hook Position cannot be null",
            hookVel, "Hook Velocity cannot be null"
        );

        // --- SWINGING STATE ---
        if (hookState.state == FishingRodState.SWINGING) {
            // Calculate the swing angle using a sine function.
            hook.swingAngle = hook.swingAmplitude * MathUtils.sin(time);
            // Use a fixed offset (or derive from attachment data if needed).
            //float offset = 80.0f;
            float offset = hookAttachment != null ? hookAttachment.offset.len() : 80f;
            float posX = hook.anchorPoint.x + offset * MathUtils.sin(hook.swingAngle);
            float posY = hook.anchorPoint.y - offset * MathUtils.cos(hook.swingAngle);
            // Update hook position.
            hookPos.pos.set(posX, posY, hookPos.pos.z);

            // Compute hook rotation from the anchor point.
            float dx = posX - hook.anchorPoint.x;
            float dy = posY - hook.anchorPoint.y;
            hookRot.angle = MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees + 90;

            // Keep reel state in sync.
            updateReelState(reelEntity, FishingRodState.SWINGING);
        }

        // Determine parameters using reel component or defaults.

        // Retrieve reel (and optionally sinker) components.
        ReelComponent reelComp = reelMapper.get(reelEntity);
        ValidateUtil.validateNotNull(reelComp, TAG + "-> reelComp");

        // Calculate hook projectile speed (hook base weight + sinker weight)
        SinkerComponent sinkerComp = (sinkerEntity != null) ? sinkerMapper.get(sinkerEntity) : null;
        WeightComponent hookWeight = weightMapper.get(hookEntity);
        float hookBaseSpeed = (hookWeight != null) ? hookWeight.weight : 0;
        float fireSpeed = HookUtil.calculateFireSpeed(hookBaseSpeed, sinkerComp);

        // --- FIRE STATE ---
        if (hookState.state == FishingRodState.FIRE) {
            if (hookPos.pos.dst(hook.anchorPoint) >= reelComp.lineLength || hook.hasAttachedEntity()) {
                hookState.changeState(FishingRodState.REELING);
                updateReelState(reelEntity, FishingRodState.REELING);
            } else {
                // Apply velocity based on hook rotation.
                hookVel.velocity.set(1f, 1f)
                    .setAngleDeg(hookRot.angle - 90);
                hookVel.velocity.scl(fireSpeed);

                // THis is handled by the PhysicalSystem
                //hookBounds.bounds.setPosition(hookPos.pos.x, hookPos.pos.y);

                updateReelState(reelEntity, FishingRodState.FIRE);
            }
        }

        float baseReturnSpeed = (reelComp != null) ? reelComp.returnSpeed : 100f;
        if (hook.hasAttachedEntity()) {
            Entity fishableEntity = hook.getAttachedEntity();
            WeightComponent fishableWeightComp = weightMapper.get(fishableEntity);
            baseReturnSpeed = HookUtil.calculateReturnSpeed(baseReturnSpeed, fishableWeightComp);
            StateComponent<FishableObjectStates> fishableState = stateMapper.get(fishableEntity);
            if (fishableState.getState().equals(FISHABLE)) {
                fishableState.changeState(HOOKED);
            }
        }

        // --- REELING STATE ---
        if (hookState.state == FishingRodState.REELING) {
            if (hookPos.pos.y < initialPosition) {
                // Reverse velocity to reel the hook back.
                hookVel.velocity.set(hook.sinkerWeight, hook.sinkerWeight)
                    .setAngleDeg(hookRot.angle - 90)
                    .scl(-baseReturnSpeed);

                updateReelState(reelEntity, FishingRodState.REELING);
            } else {
                // Transition to the RETURNED state once reeling is complete.
                hookState.changeState(FishingRodState.RETURNED);
                updateReelState(reelEntity, FishingRodState.RETURNED);
            }
        }

        // --- RETURNED STATE ---
        if (hookState.state == FishingRodState.RETURNED) {
            // If no fish is attached, the rod goes back to swinging.
            if (!hook.hasAttachedEntity()) {
                hookState.changeState(FishingRodState.SWINGING);
                updateReelState(reelEntity, FishingRodState.SWINGING);
            }
        }
    }

    private void updateReelState(Entity reelEntity, FishingRodState newRodState) {
        if (reelEntity == null) return;
        StateComponent<FishingRodState> reelState = reelEntity.getComponent(StateComponent.class);
        if (reelState == null) return;
        if (reelState.state != newRodState) {
            reelState.changeState(newRodState);
        }
    }
}
