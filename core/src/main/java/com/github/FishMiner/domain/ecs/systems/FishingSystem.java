package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.PositionComponent;
import com.github.FishMiner.domain.ecs.components.RotationComponent;

/**
 * FishingSystem is responsible for attaching the fish to the hook.
 * For each hook that has an attached fish, this system updates the fish’s PositionComponent
 * so that its position is the hook's position plus the rotated offset. Optionally, it can update
 * the fish’s rotation to match the hook.
 */
public class FishingSystem extends IteratingSystem {
    private ComponentMapper<HookComponent> hookMapper = ComponentMapper.getFor(HookComponent.class);
    private ComponentMapper<PositionComponent> posMapper = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<RotationComponent> rotMapper = ComponentMapper.getFor(RotationComponent.class);

    public FishingSystem() {
        // We iterate over hooks, as the hook holds the reference to an attached fish.
        super(Family.all(HookComponent.class, PositionComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HookComponent hook = hookMapper.get(entity);
        PositionComponent hookPos = posMapper.get(entity);

        if (hook.attachedFish != null) {
            Entity fish = hook.attachedFish;
            PositionComponent fishPos = posMapper.get(fish);
            RotationComponent fishRot = rotMapper.get(fish);

            if (fishPos != null) {
                // Compute the rotated offset based on the hook’s current swing angle.
                Vector2 rotatedOffset = new Vector2(hook.offset).rotateRad(hook.swingAngle);
                // Update the fish’s position: hook position plus the rotated offset.
                fishPos.position.set(hookPos.position).add(rotatedOffset);
            }
            if (fishRot != null) {
                // Optionally, set the fish's rotation to match the hook's rotation.
                fishRot.angle = hook.swingAngle * MathUtils.radiansToDegrees;
            }
        }
    }
}
