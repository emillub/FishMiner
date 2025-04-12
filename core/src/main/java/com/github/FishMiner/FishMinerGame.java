package com.github.FishMiner;

import static com.github.FishMiner.ui.ports.out.ScreenType.MENU;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;
import com.github.FishMiner.common.Configuration;
import com.github.FishMiner.data.handlers.LoginHandler;
import com.github.FishMiner.data.handlers.UserRegistrationHandler;
import com.github.FishMiner.data.ports.out.IAuthService;
import com.github.FishMiner.data.ports.out.ILeaderBoardService;
import com.github.FishMiner.domain.GameContext;
import com.github.FishMiner.domain.eventBus.GameEventBus;
import com.github.FishMiner.domain.managers.MusicManager;
import com.github.FishMiner.domain.managers.RequestManager;
import com.github.FishMiner.domain.managers.ScreenManager;
import com.github.FishMiner.ui.factories.ScreenFactory;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

public class FishMinerGame extends Game {
    private RequestManager requestManager;
    private final IAuthService authService;
    private final ILeaderBoardService leaderBoardService;
    private Music backgroundMusic;
    private Music playMusic;

    public FishMinerGame(IAuthService authService, ILeaderBoardService leaderboard) {
        this.authService = authService;
        this.leaderBoardService = leaderboard;

    }

    @Override
    public void create() {
        Configuration.getInstance().updateConfiguration();

        RequestManager requestManager =  new RequestManager(
            new LoginHandler(authService),
            new UserRegistrationHandler(authService)
        );

        MusicManager musicManager = new MusicManager();
        musicManager.applyVolume(Configuration.getInstance().getMusicVolume());


        ScreenFactory screenFactory = new ScreenFactory();
        ScreenManager screenManager = ScreenManager.getInstance(screenFactory, this);


        GameEventBus.getInstance().register(requestManager.getLoginRequestListener());
        GameEventBus.getInstance().register(requestManager.getRegistrationRequestListener());
        GameEventBus.getInstance().register(screenManager.getChangeScreenEvent());
        GameEventBus.getInstance().register(screenManager.getPrepareScreenEvent());
        GameEventBus.getInstance().register(musicManager);

        screenManager.switchScreenTo(MENU);
    }

    public IAuthService getAuthService() {
        return authService;
    }
    public ILeaderBoardService getLeaderBoardService() { return leaderBoardService; }


}
