package com.github.FishMiner.domain.managers;

import com.github.FishMiner.domain.ports.out.FirebaseAuthCallback;
import com.github.FishMiner.domain.eventBus.GameEventBus;
import com.github.FishMiner.domain.events.dataEvents.AuthResponseEvent;
import com.github.FishMiner.domain.ports.out.ILeaderboardFetcher;
import com.github.FishMiner.domain.ports.out.ILeaderboardPoster;
import com.github.FishMiner.domain.ports.out.ILoginHandler;
import com.github.FishMiner.domain.ports.out.IUserRegistrationHandler;
import com.github.FishMiner.domain.ports.in.IGameEventListener;
import com.github.FishMiner.ui.events.LoginRequestEvent;
import com.github.FishMiner.ui.events.RegisterUserRequest;
import com.github.FishMiner.ui.ports.in.IRequestManager;

public class RequestManager implements IRequestManager {
    private final ILoginHandler loginHandler;
    private final IUserRegistrationHandler registerHandler;

    // TODO:  add other handlers here
    //private final ILeaderboardFetcher fetchLeaderboardHandler;
    //private final ILeaderboardPoster fetchLeaderboardPoster;

    public RequestManager(
        ILoginHandler loginHandler,
        IUserRegistrationHandler registerHandler
    ) {
        this.loginHandler = loginHandler;
        this.registerHandler = registerHandler;
    }

    /**
     * Processes a login request. Sends @LoginResponseEvent to GameEventBus with either:
     * error message and success = false or user email and success = true
     *
     * @param email    the user's email
     * @param password the user's password
     */
    private void handleLoginRequest(String email, String password) {
        loginHandler.login(email, password, new FirebaseAuthCallback() {
            @Override
            public void onSuccess() {
                GameEventBus.getInstance().post(new AuthResponseEvent(email, true));
            }

            @Override
            public void onFailure(String error) {
                GameEventBus.getInstance().post(new AuthResponseEvent(error, false));
            }
        });
    }

    /**
     * Processes a registration request. Sends a RegisterResponseEvent to the GameEventBus with either:
     * - the user's email and success = true
     * - an error message and success = false
     *
     * @param email    the user's email
     * @param password the user's password
     */
    private void handleRegisterRequest(String email, String password) {
        registerHandler.register(email, password, new FirebaseAuthCallback() {
            @Override
            public void onSuccess() {
                GameEventBus.getInstance().post(new LoginRequestEvent(email, password));
                GameEventBus.getInstance().post(new AuthResponseEvent(email, true));
            }

            @Override
            public void onFailure(String error) {
                GameEventBus.getInstance().post(new AuthResponseEvent(error, false));
            }
        });
    }

    /**
     * Returns an event listener for Login requests.
     */
    public IGameEventListener<LoginRequestEvent> getLoginRequestListener() {
        return new IGameEventListener<LoginRequestEvent>() {
            @Override
            public void onEvent(LoginRequestEvent event) {
                handleLoginRequest(event.getEmail(), event.getPassword());
            }
            @Override
            public Class<LoginRequestEvent> getEventType() {
                return LoginRequestEvent.class;
            }
        };
    }

    /**
     * Returns an event listener for registration requests.
     */
    public IGameEventListener<RegisterUserRequest> getRegistrationRequestListener() {
        return new IGameEventListener<RegisterUserRequest>() {
            @Override
            public void onEvent(RegisterUserRequest event) {
                handleRegisterRequest(event.getEmail(), event.getPassword());
            }

            @Override
            public Class<RegisterUserRequest> getEventType() {
                return RegisterUserRequest.class;
            }
        };
    }

}
