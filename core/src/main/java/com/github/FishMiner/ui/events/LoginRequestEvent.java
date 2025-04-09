package com.github.FishMiner.ui.events;

import com.github.FishMiner.domain.ports.in.IGameEvent;

public class LoginRequestEvent implements IGameEvent {
    private String playerID;
    private String password;
    private boolean handled;
    public LoginRequestEvent(String email, String password) {
        playerID = email;
        this.password = password;
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getPassword() {
        return password;
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
