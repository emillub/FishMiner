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

        if (attachment.getParent() == null) return;

        TransformComponent parentTransform = transformMapper.get(attachment.getParent());
        if (parentTransform == null) return;


        // Update child's position relative to parent
        childTransform.pos.set(parentTransform.pos).add(attachment.offset);
    }


}
