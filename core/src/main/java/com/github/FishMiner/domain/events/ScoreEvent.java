package com.github.FishMiner.domain.events;

import com.github.FishMiner.Logger;

public class ScoreEvent implements IGameEvent {
    private final static String TAG = "ScoreEvent";
    private boolean handled;
    private final float value;

    public ScoreEvent(float scoreDifference) {
        Logger.getInstance().debug(TAG, "Score increase: " + scoreDifference);
        this.value = scoreDifference;
        this.handled = false;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    public float getValue() {
        return value;
    }

    public boolean isHandled() {
        return handled;
    }

}
