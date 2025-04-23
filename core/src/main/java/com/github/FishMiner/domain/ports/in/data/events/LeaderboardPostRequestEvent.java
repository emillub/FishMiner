package com.github.FishMiner.domain.ports.in.data.events;

import com.github.FishMiner.domain.ports.in.ui.interfaces.IGameEvent;
import com.github.FishMiner.data.ScoreEntry;

public class LeaderboardPostRequestEvent implements IGameEvent {
    private final ScoreEntry scoreEntry;
    private boolean handled = false;

    public LeaderboardPostRequestEvent(ScoreEntry scoreEntry) {
        this.scoreEntry = scoreEntry;
    }

    public ScoreEntry getScoreEntry() {
        return scoreEntry;
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
