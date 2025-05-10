package com.github.FishMiner.domain.ports.in.data.events;

import com.github.FishMiner.domain.ports.out.data.interfaces.IDataRequestEvent;

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

