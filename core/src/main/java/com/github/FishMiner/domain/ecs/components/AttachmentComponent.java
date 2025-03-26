package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Null;

public class AttachmentComponent implements Component {
    @Null
    public Entity parent;


    // TODO: find out if this should be vector2 or 3
    // if vector2 -> public Vector2 offset = new Vector2(0, 0);
    public Vector3 offset = new Vector3(0, 0, 0);

    public void setParentEntity(Entity parent) {
        this.parent = parent;
    }

    public Entity getParent() {
        return parent;
    }

}
