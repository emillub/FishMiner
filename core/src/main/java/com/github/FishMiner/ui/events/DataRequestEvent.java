package com.github.FishMiner.ui.events;

import com.github.FishMiner.domain.ports.in.IDataRequestEvent;

public abstract class DataRequestEvent implements IDataRequestEvent {

    private String email;
    private final String password;
    public DataRequestEvent(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}

