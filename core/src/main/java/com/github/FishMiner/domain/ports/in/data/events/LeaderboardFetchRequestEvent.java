package com.github.FishMiner.domain.ports.in.data.events;

import com.github.FishMiner.domain.ports.in.ui.interfaces.IGameEvent;

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
