package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

public class AttachmentComponent implements Component {
    public Entity parent;
    public Vector2 offset;

    public AttachmentComponent(Vector2 offset, Entity parent) {
        this.offset = offset;
        this.parent = parent;
    }

    public AttachmentComponent(Vector2 offset) {
        this(offset, null);
    }

    public void setParent(Entity parent) {
        this.parent = parent;
    }

    public Entity getParent() {
        return parent;
    }

}
