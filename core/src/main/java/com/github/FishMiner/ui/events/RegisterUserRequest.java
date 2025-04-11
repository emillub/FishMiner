package com.github.FishMiner.ui.events;

import com.github.FishMiner.domain.ports.in.IGameEvent;

public class RegisterUserRequest extends DataRequestEvent implements IGameEvent {
    private boolean handled;
    public RegisterUserRequest(String email, String password) {
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
