package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.github.FishMiner.common.Logger;

public class FishingRodComponent implements Component {
    private static final String TAG = "PlayerComponent";
    private Entity hook;
    private Entity reel;
    private Entity sinker;

    public Vector3 hookAnchorPoint = new Vector3();
    public Vector3 reelAnchorPoint = new Vector3();
    public Vector3 sinkerAnchorPoint = new Vector3();

    public void setHook(Entity hook) {
        if (this.hook != null) Logger.getInstance().log(TAG, "Hook was replaced");
        this.hook = hook;
    }

    public void setReel(Entity reel) {
        if (this.reel != null) Logger.getInstance().log(TAG, "Reel was replaced");
        this.reel = reel;
    }

    public void setSinker(Entity sinker) {
        if (this.sinker != null) Logger.getInstance().log(TAG, "Sinker was replaced");
        this.sinker = sinker;
    }

    public Entity getHook() {
        return hook;
    }

    public Entity getReel() {
        return reel;
    }

    public Entity getSinker() {
        return sinker;
    }

}
