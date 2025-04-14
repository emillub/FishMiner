package com.github.FishMiner.domain.managers;

import com.github.FishMiner.data.ScoreEntry;
import com.github.FishMiner.domain.events.data.LeaderboardResponseEvent;
import com.github.FishMiner.domain.ports.in.data.FirebaseAuthCallback;
import com.github.FishMiner.domain.GameEventBus;
import com.github.FishMiner.domain.events.data.AuthResponseEvent;
import com.github.FishMiner.domain.ports.in.data.LeaderboardCallback;
import com.github.FishMiner.domain.ports.out.ILeaderboardFetcher;
import com.github.FishMiner.domain.ports.out.ILeaderboardPoster;
import com.github.FishMiner.domain.ports.out.ILoginHandler;
import com.github.FishMiner.domain.ports.out.IUserRegistrationHandler;
import com.github.FishMiner.domain.ports.in.IGameEventListener;
import com.github.FishMiner.ui.events.data.LeaderboardFetchRequestEvent;
import com.github.FishMiner.ui.events.data.LeaderboardPostRequestEvent;
import com.github.FishMiner.ui.events.data.LoginRequestEvent;
import com.github.FishMiner.ui.events.data.RegisterUserRequest;
import com.github.FishMiner.ui.ports.in.IRequestManager;

import java.util.List;

public class RequestManager implements IRequestManager {
    private final ILoginHandler loginHandler;
    private final IUserRegistrationHandler registerHandler;

    private final ILeaderboardFetcher leaderboardFetcher;
    private final ILeaderboardPoster leaderboardPoster;


    // TODO:  add other handlers here
    //private final ILeaderboardFetcher fetchLeaderboardHandler;
    //private final ILeaderboardPoster fetchLeaderboardPoster;

    public RequestManager(
        ILoginHandler loginHandler,
        IUserRegistrationHandler registerHandler,
        ILeaderboardFetcher leaderboardFetcher,
        ILeaderboardPoster leaderboardPoster
    ) {
        this.loginHandler = loginHandler;
        this.registerHandler = registerHandler;
        this.leaderboardFetcher = leaderboardFetcher;
        this.leaderboardPoster = leaderboardPoster;
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

    private void handleLeaderboardFetchRequest() {
        leaderboardFetcher.fetchLeaderboard(new LeaderboardCallback() {
            @Override
            public void onSuccess(List<ScoreEntry> topScores) {
                if (topScores != null) {
                    GameEventBus.getInstance().post(new LeaderboardResponseEvent(topScores));
                } else {
                    System.out.println("Warning: leaderboardService.getTopScores returned null.");
                    GameEventBus.getInstance().post(new LeaderboardResponseEvent("Score list was null."));
                }            }

            @Override
            public void onFailure(String errorMessage) {
                GameEventBus.getInstance().post(new LeaderboardResponseEvent(errorMessage));
            }
        });
    }

    private void handleLeaderboardPostRequest(ScoreEntry entry) {
        leaderboardPoster.postScore(entry, new LeaderboardCallback() {
            @Override
            public void onSuccess(List<ScoreEntry> updatedScores) {
                GameEventBus.getInstance().post(new LeaderboardResponseEvent(updatedScores));
            }

            @Override
            public void onFailure(String errorMessage) {
                GameEventBus.getInstance().post(new LeaderboardResponseEvent(errorMessage));
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

    public IGameEventListener<LeaderboardFetchRequestEvent> getLeaderboardFetchRequestListener() {
        return new IGameEventListener<>() {
            @Override
            public void onEvent(LeaderboardFetchRequestEvent event) {
                handleLeaderboardFetchRequest();
            }

            @Override
            public Class<LeaderboardFetchRequestEvent> getEventType() {
                return LeaderboardFetchRequestEvent.class;
            }
        };
    }
    public IGameEventListener<LeaderboardPostRequestEvent> getLeaderboardPostRequestListener() {
        return new IGameEventListener<>() {
            @Override
            public void onEvent(LeaderboardPostRequestEvent event) {
                handleLeaderboardPostRequest(event.getScoreEntry());
            }

            @Override
            public Class<LeaderboardPostRequestEvent> getEventType() {
                return LeaderboardPostRequestEvent.class;
            }
        };
    }



}
