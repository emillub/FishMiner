package com.github.FishMiner.domain.ports.out.ui.events;

import com.github.FishMiner.infrastructure.Logger;
import com.github.FishMiner.infrastructure.ports.in.IGameEvent;

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
