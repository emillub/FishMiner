package com.github.FishMiner.domain.events.data;

import com.github.FishMiner.common.ValidateUtil;
import com.github.FishMiner.domain.ports.in.IGameEvent;

public class AuthResponseEvent implements IGameEvent {
    private static final String TAG = "AuthResponseEvent";
    private final boolean success;
    private boolean handled;
    private String error = null;
    private String email = null;

    /**
     * Constructs an AuthResponseEvent.
     * @param response the email on success or error message on failure
     * @param wasSuccess true if authentication succeeded, false otherwise
     */
    public AuthResponseEvent(String response, boolean wasSuccess) {
        this.success = wasSuccess;
        if (wasSuccess) {
            setEmail(response);
        } else {
            setError(response);
        }
    }

    public void setEmail(String email) {
        ValidateUtil.validateNotNull(email, TAG + ".setEmail(email) -> email");
        this.email = email;
    }

    public void setError(String error) {
        ValidateUtil.validateNotNull(error, TAG + ".setError(error) -> error");
        this.error = error;
    }

    public String getEmail() {
        ValidateUtil.validateNotNull(email, TAG + ".getEmail()");
        return email;
    }

    public String getError() {
        ValidateUtil.validateNotNull(error, TAG + ".getError()");
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
