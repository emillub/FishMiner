package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.github.FishMiner.infrastructure.Configuration;
import com.github.FishMiner.infrastructure.Logger;
import com.github.FishMiner.infrastructure.ValidateUtil;
import com.github.FishMiner.domain.ecs.components.AttachmentComponent;
import com.github.FishMiner.domain.ecs.components.BoundsComponent;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.PlayerComponent;
import com.github.FishMiner.domain.ecs.components.ReelComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.RotationComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;
import com.github.FishMiner.domain.ecs.components.WeightComponent;
import com.github.FishMiner.domain.ecs.states.HookStates;

public class HookSystem extends IteratingSystem {
    private boolean initialized = false;
    private final ComponentMapper<HookComponent> hm = ComponentMapper.getFor(HookComponent.class);
    private final ComponentMapper<TransformComponent> pm = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<RotationComponent> rm = ComponentMapper.getFor(RotationComponent.class);
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private final ComponentMapper<StateComponent> sm = ComponentMapper.getFor(StateComponent.class);
    private final ComponentMapper<BoundsComponent> bm = ComponentMapper.getFor(BoundsComponent.class);
    private final ComponentMapper<AttachmentComponent> am = ComponentMapper.getFor(AttachmentComponent.class);
    private final ComponentMapper<ReelComponent> reelMapper = ComponentMapper.getFor(ReelComponent.class);
    private final ComponentMapper<WeightComponent> wm = ComponentMapper.getFor(WeightComponent.class);


    private float initialPosition;
    private float time;

    public HookSystem() {
        super(Family.all(HookComponent.class, TransformComponent.class, BoundsComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        time += deltaTime;
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HookComponent hook = hm.get(entity);
        TransformComponent hookPos = pm.get(entity);
        RotationComponent hookRot = rm.get(entity);
        VelocityComponent hookVel = vm.get(entity);
        StateComponent<HookStates> hookState = sm.get(entity);
        BoundsComponent hookBounds = bm.get(entity);
        AttachmentComponent hookAttachment = am.get(entity);

        if (hookAttachment == null) {
            Logger.getInstance().error("HookSystem", "Missing AttachmentComponent on hook entity!");
            return;
        }


        Entity player = hookAttachment.getParent();
        if (player == null || !pm.has(player)) {
            System.err.println("[HookSystem] ERROR: Player entity is missing PlayerComponent.");
            return;
        }        PlayerComponent playerComponent = player.getComponent(PlayerComponent.class);
        if (playerComponent == null) {
            Logger.getInstance().error("HookSystem", "Player entity is missing PlayerComponent.");
            return;
        }
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

        // Find sinker entity attached to the hook
        Entity sinkerEntity = null;
        for (Entity possibleSinker : getEngine().getEntities()) {
            AttachmentComponent att = am.get(possibleSinker);
            if (att != null && att.getParent() == entity) {  // entity is the hook
                sinkerEntity = possibleSinker;
                break;
            }
        }
        float sinkerWeight = 1.0f; // default
        if (sinkerEntity != null && wm.has(sinkerEntity)) {
            sinkerWeight = wm.get(sinkerEntity).weight;
        }



        if (hookState.state == HookStates.SWINGING) {
            hook.swingAngle = hook.swingAmplitude * MathUtils.sin(time);
            float offset = 80.0f;
            float posX = hook.anchorPoint.x + offset * MathUtils.sin(hook.swingAngle);
            float posY = hook.anchorPoint.y - offset * MathUtils.cos(hook.swingAngle);
            hookPos.pos.set(posX, posY, 1);

            float dx = posX - hook.anchorPoint.x;
            float dy = posY - hook.anchorPoint.y;
            hookRot.angle = MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees + 90;

            updateReelState(reelEntity, HookStates.SWINGING);
        }

        float lineLength = (reel != null) ? reel.lineLength : 100f;
        float returnSpeed = (reel != null) ? reel.returnSpeed : 100f;

        if (hookState.state == HookStates.FIRE) {
            if (hookPos.pos.dst(hook.anchorPoint) >= lineLength || hook.attachedFishableEntity != null) {
                hookState.changeState(HookStates.REELING);
                updateReelState(reelEntity, HookStates.REELING);
            } else {
                hookVel.velocity.set(sinkerWeight, sinkerWeight).setAngleDeg(hookRot.angle - 90);
                hookVel.velocity.scl(returnSpeed * Configuration.getInstance().getScaleY() * 4);
                hookBounds.bounds.setPosition(hookPos.pos.x, hookPos.pos.y);

                updateReelState(reelEntity, HookStates.FIRE); // 🔁 animate while firing down
            }
        }

        if (hookState.state == HookStates.REELING) {
            if (hookPos.pos.y < initialPosition) {
                hookVel.velocity.set(sinkerWeight, sinkerWeight)
                    .setAngleDeg(hookRot.angle - 90)
                        .scl(-returnSpeed * Configuration.getInstance().getScaleY() * 2);

                updateReelState(reelEntity, HookStates.REELING);
            } else {
                hookState.changeState(HookStates.RETURNED);
                updateReelState(reelEntity, HookStates.RETURNED);
            }
        }

        if (hookState.state == HookStates.RETURNED) {
            if (!hook.hasAttachedEntity()) {
                hookState.changeState(HookStates.SWINGING);
                updateReelState(reelEntity, HookStates.SWINGING);
            }
        }
    }

    private void updateReelState(Entity reelEntity, HookStates newHookState) {
        if (reelEntity == null) return;

        StateComponent<HookStates> reelState = reelEntity.getComponent(StateComponent.class);
        if (reelState == null) return;

        if (reelState.state != newHookState) {
            reelState.changeState(newHookState);
        }
    }
}
