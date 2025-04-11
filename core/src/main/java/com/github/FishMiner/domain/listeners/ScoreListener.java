package com.github.FishMiner.domain.listeners;

import com.github.FishMiner.domain.events.ScoreEvent;
import com.github.FishMiner.domain.ports.in.IGameEventListener;

public class ScoreListener implements IGameEventListener<ScoreEvent> {
    private float currentScore = -1;
    private float prevScore = -1;

    @Override
    public void onEvent(ScoreEvent event) {
        if (event.isHandled()) return;
        if (currentScore != -1) {
            prevScore = currentScore;
        }
        currentScore = event.getScore();
        event.setHandled();
    }

    public float getCurrentScore() {
        return currentScore;
    }

    public float getPrevScore() {
        return prevScore;
    }

    @Override
    public Class<ScoreEvent> getEventType() {
        return ScoreEvent.class;
    }
}
