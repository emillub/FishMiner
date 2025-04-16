package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector3;
import com.github.FishMiner.common.Logger;
import com.github.FishMiner.domain.ecs.components.AttachmentComponent;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.VelocityComponent;

public class AttachmentSystem extends IteratingSystem {
    private static final String TAG = "AttachmentSystem";
    private final ComponentMapper<AttachmentComponent> attachmentMapper = ComponentMapper.getFor(AttachmentComponent.class);
    private final ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<VelocityComponent> velocityMapper = ComponentMapper.getFor(VelocityComponent.class);

    public AttachmentSystem() {
        super(Family.all(AttachmentComponent.class, TransformComponent.class).exclude(HookComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AttachmentComponent attachment = attachmentMapper.get(entity);
        TransformComponent childTransform = transformMapper.get(entity);
        VelocityComponent childVelocity = velocityMapper.get(entity);
        if (childVelocity != null && childVelocity.velocity.x != 0) {
            return;
        }
        if (attachment.getParent() == null) {
            return;
        }
        TransformComponent parentTransform = transformMapper.get(attachment.getParent());
        if (parentTransform == null) {
            Logger.getInstance().debug(TAG, "Entity with attachment is missing transformcomponent");
            return;
        }
        // Update child's position relative to parent's position plus the offset.
        Vector3 newPos = new Vector3(
            parentTransform.pos.x + attachment.offset.x,
            parentTransform.pos.y + attachment.offset.y,
            parentTransform.pos.z + attachment.offset.z
        );
        childTransform.pos.set(newPos);
    }
}
