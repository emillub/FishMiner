package com.github.FishMiner.ui.events.data;

import com.github.FishMiner.domain.ports.in.IGameEvent;

public class LoginRequestEvent extends DataRequestEvent implements IGameEvent {

    private boolean handled;
    public LoginRequestEvent(String email, String password) {
        super(email, password);
        handled = false;
    }

    @Override
    public void setHandled() {
        this.handled = true;
    }

    @Override
    public boolean isHandled() {
        return handled;
    }
}
