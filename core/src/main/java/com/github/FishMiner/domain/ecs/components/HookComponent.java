package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;


public class HookComponent implements Component {
    public float swingAngle;
    public float swingAmplitude;

    public Entity attachedFishableEntity;

    public Vector2 offset;

    public HookComponent(float swingAngle, float swingAmplitude) {
        this.swingAngle = swingAngle;
        this.swingAmplitude = swingAmplitude;
        this.offset = new Vector2();
    }

    public void attachEntity(Entity fishableEntity) {
        this.attachedFishableEntity = fishableEntity;
    }

    public void detachEntity() {
        this.attachedFishableEntity = null;
    }

    public boolean hasAttachedEntity() {
        return attachedFishableEntity != null;
    }
}
