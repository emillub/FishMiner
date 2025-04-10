package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.util.FishUtils;


public class HookComponent implements Component {
    public float swingAngle = 0.5f;
    public float swingAmplitude = 0.5f;
    public Entity attachedFishableEntity;
    public Vector3 offset = new Vector3(0, -2, 1);

    // temp reelLength. This actually belongs to the ReelComponent
    public final float reelLength = FishUtils.getDepthIntervalFor(1)[1];

    public final int sinkerWeight = 2;

    public final float swingOffset = 1;

    public Vector3 anchorPoint = new Vector3();

    //public final Vector3 anchorPoint = new Vector3(
    //    (float) (Configuration.getInstance().getScreenWidth() / 2),
    //    (float) (Configuration.getInstance().getScreenHeight() - 200),
    //    0
    //);

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
