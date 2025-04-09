package com.github.FishMiner.domain.handlers;

import com.github.FishMiner.domain.eventBus.GameEventBus;
import com.github.FishMiner.domain.events.LoginResponseEvent;
import com.github.FishMiner.domain.ports.in.IGameEventListener;
import com.github.FishMiner.ui.events.LoginRequestEvent;

public class RequestManager implements IGameEventListener<LoginRequestEvent> {
    private final ILoginHandler loginHandler;
    // In a full implementation, other handlers (e.g., IRegisterUserHandler, ILeaderboardFetcher, etc.) can be added.

    public RequestManager(ILoginHandler loginHandler) {
        this.loginHandler = loginHandler;
    }

    /**
     * Processes a login request. Sends @LoginResponseEvent to GameEventBus with either:
     * error message and success = false or user email and success = true
     *
     * @param email    the user's email
     * @param password the user's password
     */
    public void handleLoginRequest(String email, String password) {
        loginHandler.login(email, password, new LoginCallBack() {
            @Override
            public void onSuccess() {
                GameEventBus.getInstance().post(new LoginResponseEvent(email, true));
            }

            @Override
            public void onFailure(String error) {
                GameEventBus.getInstance().post(new LoginResponseEvent(error, false));
            }
        });
    }

    @Override
    public void onEvent(LoginRequestEvent event) {
        handleLoginRequest(event.getPlayerID(), event.getPassword());
    }

    @Override
    public Class<LoginRequestEvent> getEventType() {
        return LoginRequestEvent.class;
    }
}
