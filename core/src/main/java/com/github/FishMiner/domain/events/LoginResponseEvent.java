package com.github.FishMiner.domain.events;

import com.github.FishMiner.domain.ecs.util.ValidateUtil;
import com.github.FishMiner.domain.ports.in.IGameEvent;

public class LoginResponseEvent implements IGameEvent {
    private static final String TAG = "LoginResponseEvent";
    private final Boolean success;
    private boolean handled;
    private String error = null;
    private String email = null;

    public LoginResponseEvent(String response, boolean wasSuccess) {
        if (wasSuccess) {
            setLoginEmail(response);
        } else {
            setError(response);
        }
        success = wasSuccess;
    }

    public void setLoginEmail(String email) {
        ValidateUtil.validateNotNull(email,  TAG + ".setEmail(email) -> email");
        this.email = email;
    }

    public void setError(String error) {
        ValidateUtil.validateNotNull(error,  TAG + ".setResponse(error) -> error");
        this.error = error;
    }

    public String getEmail() {
        ValidateUtil.validateNotNull(email, TAG + ".getEmail()");
        return email;
    }

    public String getResponseError() {
        ValidateUtil.validateNotNull(error, TAG + ".getResponseError()");
        return error;
    }

    public boolean wasSuccessful() {
        return success;
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
