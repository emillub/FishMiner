package com.github.FishMiner.domain.events.uiEvents;

import com.github.FishMiner.domain.ports.in.IGameEvent;

public class DisplayScoreValueEvent implements IGameEvent {
    public final float value;
    public final float x;
    public final float y;
    private boolean handled = false;

    public DisplayScoreValueEvent(float value, float x, float y) {
        this.value = value;
        this.x = x;
        this.y = y;
    }

    @Override
    public void setHandled() {
        handled = true;
    }

    @Override
    public boolean isHandled() {
        return handled;
    }
}
