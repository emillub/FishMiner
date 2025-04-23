package com.github.FishMiner.domain.ports.in.data.events;

import com.github.FishMiner.domain.ports.in.ui.interfaces.IGameEvent;
import com.github.FishMiner.data.ScoreEntry;

import java.util.List;

public class LeaderboardResponseEvent implements IGameEvent {
    private final boolean success;
    private final List<ScoreEntry> scores;
    private final String errorMessage;
    private boolean handled = false;

    // Success constructor
    public LeaderboardResponseEvent(List<ScoreEntry> scores) {
        this.success = true;
        this.scores = scores;
        this.errorMessage = null;
    }

    // Failure constructor
    public LeaderboardResponseEvent(String errorMessage) {
        this.success = false;
        this.errorMessage = errorMessage;
        this.scores = null;
    }

    public boolean isSuccess() {
        return success;
    }

    public List<ScoreEntry> getScores() {
        return scores;
    }

    public String getErrorMessage() {
        return errorMessage;
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
