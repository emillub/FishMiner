package com.github.FishMiner.domain.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector3;
import com.github.FishMiner.domain.ecs.components.AttachmentComponent;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;

public class AttachmentSystem extends IteratingSystem {
    private final ComponentMapper<AttachmentComponent> attachmentMapper = ComponentMapper.getFor(AttachmentComponent.class);
    private final ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);

    public AttachmentSystem() {
        super(Family.all(AttachmentComponent.class, TransformComponent.class).exclude(HookComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        AttachmentComponent attachment = attachmentMapper.get(entity);
        TransformComponent childTransform = transformMapper.get(entity);
        if (attachment.getParent() == null) {
            return; // Nothing to attach to.
        }
        TransformComponent parentTransform = transformMapper.get(attachment.getParent());
        if (parentTransform == null) {
            return; // Parent does not have a transform.
        }
        // Update child's position relative to parent's position plus the offset.
        Vector3 newPos = new Vector3(
            parentTransform.pos.x + attachment.offset.x,
            parentTransform.pos.y + attachment.offset.y,
            parentTransform.pos.z + attachment.offset.z
        ).add(attachment.offset);
        childTransform.pos.set(newPos);
    }
}
