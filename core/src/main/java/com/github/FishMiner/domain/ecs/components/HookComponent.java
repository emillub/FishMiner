package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.github.FishMiner.domain.ecs.utils.DomainUtils;


public class HookComponent implements Component {
    public float swingAngle = 0.5f;
    public float swingAmplitude = 1.1f;
    public Entity attachedFishableEntity;
    public Vector3 offset = new Vector3(0, -2, 1);
    public String name;

    public final int sinkerWeight = 1;

    public final float swingOffset = 1;

    public Vector3 anchorPoint = new Vector3();

    public void attachEntity(Entity fishableEntity) {
        this.attachedFishableEntity = fishableEntity;
    }

    public void detachEntity() {
        this.attachedFishableEntity = null;
    }

    public boolean hasAttachedEntity() {
        return attachedFishableEntity != null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
