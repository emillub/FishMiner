package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;


public class HookComponent implements Component {
    public float swingAngle;
    public float swingAmplitude;

    public Entity attachedFish;

    public Vector2 offset;

    //public enum HookState { SWINGING, FIRED , REELING }
    //public HookState state = HookState.SWINGING;


    public HookComponent(float swingAngle, float swingAmplitude) {
        this.swingAngle = swingAngle;
        this.swingAmplitude = swingAmplitude;
        this.offset = new Vector2();
    }

    public void attachFish(Entity fishableEntity) {
        this.attachedFish = fishableEntity;
    }

    public void detachFish() {
        this.attachedFish = null;
    }

    public boolean isFishAttached() {
        return attachedFish != null;
    }

}
