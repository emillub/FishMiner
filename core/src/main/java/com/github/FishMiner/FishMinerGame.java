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

    /*

    public FishMinerGame(IAuthService authService, ILeaderBoardService leaderboard) {
        this.authService = authService;
        this.leaderBoardService = leaderboard;

    }

     */

    public FishMinerGame(IAuthService authService, ILeaderBoardService leaderboard) {
        this(authService, leaderboard, null); // fallback
    }

    public FishMinerGame(IAuthService authService, ILeaderBoardService leaderboard, RequestManager requestManager) {
        this.authService = authService;
        this.leaderBoardService = leaderboard;
        this.requestManager = requestManager;
    }

    @Override
    public void create() {
        System.out.println("[DEBUG] FishMinerGame create() started");
        Configuration.getInstance().updateConfiguration();
        // Load all buttons
        for (ButtonEnum Button : Assets.ButtonEnum.values()) {
            for (ButtonStateEnum buttonState : Assets.ButtonStateEnum.values()) {
                Assets.getInstance().loadAsset(Assets.BYTTON_PATHS.get(Button).get(buttonState), Texture.class);
            }
        }

        Assets.getInstance().loadAsset(Assets.PLAYER_TEXTURE, Texture.class);
        Assets.getInstance().loadAsset(Assets.TITLE_PATH, Texture.class);

        RequestManager managerToUse = (requestManager != null)
            ? requestManager
            : new RequestManager(
            new LoginHandler(authService),
            new UserRegistrationHandler(authService),
            new LeaderboardFetcher(leaderBoardService),
            new LeaderboardPoster(leaderBoardService)
        );

        /*
         requestManager = new RequestManager(
            new LoginHandler(authService),
            new UserRegistrationHandler(authService),
            new LeaderboardFetcher(leaderBoardService), // we’ll make this class next
            new LeaderboardPoster(leaderBoardService)   // and this one too
        );

         */


        MusicManager musicManager = MusicManager.getInstance();
        ScreenFactory screenFactory = new ScreenFactory();
        ScreenManager screenManager = ScreenManager.getInstance(screenFactory, this);


        GameEventBus.getInstance().register(managerToUse.getLoginRequestListener());
        GameEventBus.getInstance().register(managerToUse.getRegistrationRequestListener());
        GameEventBus.getInstance().register(managerToUse.getLeaderboardFetchRequestListener());
        GameEventBus.getInstance().register(managerToUse.getLeaderboardPostRequestListener());
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
