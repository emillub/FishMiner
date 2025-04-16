package com.github.FishMiner;

import static com.github.FishMiner.ui.ports.out.ScreenType.MENU;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.github.FishMiner.common.Assets;
import com.github.FishMiner.common.Configuration;
import com.github.FishMiner.common.Assets.ButtonEnum;
import com.github.FishMiner.common.Assets.ButtonStateEnum;
import com.github.FishMiner.data.handlers.LeaderboardFetcher;
import com.github.FishMiner.data.handlers.LeaderboardPoster;
import com.github.FishMiner.data.handlers.LoginHandler;
import com.github.FishMiner.data.handlers.UserRegistrationHandler;
import com.github.FishMiner.data.ports.out.IAuthService;
import com.github.FishMiner.data.ports.out.ILeaderBoardService;
import com.github.FishMiner.domain.GameEventBus;
import com.github.FishMiner.domain.events.soundEvents.MusicEvent;
import com.github.FishMiner.domain.managers.MusicManager;
import com.github.FishMiner.domain.managers.RequestManager;
import com.github.FishMiner.domain.managers.ScreenManager;
import com.github.FishMiner.ui.factories.ScreenFactory;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

public class FishMinerGame extends Game {
    private RequestManager requestManager;
    private final IAuthService authService;
    private final ILeaderBoardService leaderBoardService;

    public FishMinerGame(IAuthService authService, ILeaderBoardService leaderboard) {
        this.authService = authService;
        this.leaderBoardService = leaderboard;

    }

    @Override
    public void create() {
        Configuration.getInstance().updateConfiguration();
        // Load all buttons
        for (ButtonEnum Button : Assets.ButtonEnum.values()) {
            for (ButtonStateEnum buttonState : Assets.ButtonStateEnum.values()) {
                Assets.getInstance().loadAsset(Assets.BYTTON_PATHS.get(Button).get(buttonState), Texture.class);
            }
        }

        requestManager = new RequestManager(
            new LoginHandler(authService),
            new UserRegistrationHandler(authService),
            new LeaderboardFetcher(leaderBoardService), // we’ll make this class next
            new LeaderboardPoster(leaderBoardService)   // and this one too
        );


        MusicManager musicManager = MusicManager.getInstance();
        ScreenFactory screenFactory = new ScreenFactory();
        ScreenManager screenManager = ScreenManager.getInstance(screenFactory, this);


        GameEventBus.getInstance().register(requestManager.getLoginRequestListener());
        GameEventBus.getInstance().register(requestManager.getRegistrationRequestListener());
        GameEventBus.getInstance().register(requestManager.getLeaderboardFetchRequestListener());
        GameEventBus.getInstance().register(requestManager.getLeaderboardPostRequestListener());
        GameEventBus.getInstance().register(screenManager.getChangeScreenEvent());
        GameEventBus.getInstance().register(screenManager.getPrepareScreenEvent());
        GameEventBus.getInstance().register(musicManager);
        GameEventBus.getInstance().post(new MusicEvent(MusicEvent.MusicCommand.PLAY_BACKGROUND));
        Assets.getInstance().finishLoading();
        screenManager.switchScreenTo(MENU);
    }

    public IAuthService getAuthService() {
        return authService;
    }
    public ILeaderBoardService getLeaderBoardService() { return leaderBoardService; }


}
