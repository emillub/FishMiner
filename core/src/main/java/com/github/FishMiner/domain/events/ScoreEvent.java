package com.github.FishMiner.domain.events;

public class ScoreEvent implements IGameEvent {
    private boolean handled;
    private final float value;

    public ScoreEvent(float scoreDifference) {
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
