package com.github.FishMiner.ui.events.data;

import com.github.FishMiner.domain.ports.in.IGameEvent;

public class LeaderboardFetchRequestEvent implements IGameEvent {
    private boolean handled = false;

    @Override
    public void setHandled() {
        handled = true;
    }

    @Override
    public boolean isHandled() {
        return handled;
    }
}
