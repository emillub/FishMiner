package com.github.FishMiner.domain.events;

import com.github.FishMiner.common.Logger;
import com.github.FishMiner.domain.ports.in.IGameEvent;

public class ScoreEvent implements IGameEvent {
    private final static String TAG = "ScoreEvent";
    private boolean handled;
    private final float score;

    public ScoreEvent(float scoreDifference) {
        Logger.getInstance().debug(TAG, "Score increase: " + scoreDifference);
        this.score = scoreDifference;
        this.handled = false;
    }

    public void setHandled() {
        this.handled = true;
    }

    public float getScore() {
        return score;
    }

    public boolean isHandled() {
        return handled;
    }

}
