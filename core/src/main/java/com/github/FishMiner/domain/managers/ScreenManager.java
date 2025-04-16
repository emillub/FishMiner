package com.github.FishMiner.domain.managers;

import static com.github.FishMiner.ui.ports.out.ScreenType.MENU;
import static com.github.FishMiner.ui.ports.out.ScreenType.PLAY;

import com.badlogic.gdx.Screen;
import com.github.FishMiner.FishMinerGame;
import com.github.FishMiner.common.Logger;
import com.github.FishMiner.domain.GameContext;
import com.github.FishMiner.common.ValidateUtil;
import com.github.FishMiner.domain.events.screenEvents.ChangeScreenEvent;
import com.github.FishMiner.domain.events.screenEvents.PrepareScreenEvent;
import com.github.FishMiner.domain.ports.in.IGameEventListener;
import com.github.FishMiner.domain.ports.in.IGameScreen;
import com.github.FishMiner.domain.ports.in.IScreenFactory;
import com.github.FishMiner.ui.ports.out.ScreenType;

import java.util.HashMap;

public class ScreenManager {
    private final static String TAG = "ScreenManager";
    private static ScreenManager instance;
    private final FishMinerGame game;
    private final IScreenFactory screenFactory;
    private final GameContext gameContext;
    private IGameScreen currentScreen;
    private final HashMap<ScreenType, Screen> cachedScreens = new HashMap<>();

    private ScreenManager(IScreenFactory screenFactory, FishMinerGame game, GameContext gameContext) {
        ValidateUtil.validateMultipleNotNull(
            screenFactory, "screenFactory",
            game, "fishMinerGame",
            gameContext, "gameContext"
        );
        this.gameContext = gameContext;
        this.screenFactory = screenFactory;
        this.game = game;
        gameContext.setScreenManager(this);
    }

    public ScreenType getCurrentScreenType() {
        return currentScreen.getScreenType();
    }

    public static ScreenManager getInstance(IScreenFactory screenFactory, FishMinerGame game) {
        if (instance == null) {
            GameContext gameContext = new GameContext();
            instance = new ScreenManager(screenFactory, game, gameContext);
        }
        return instance;
    }

    public static ScreenManager getInstance() {
        try {
            ValidateUtil.validateNotNull(instance, "ScreenManager.instance");
            return instance;
        } catch (IllegalArgumentException e) {
            IllegalStateException exception = new IllegalStateException("ScreenManager cannot be null");
            Logger.getInstance().error(TAG, "ScreenManager is not initialized. Initialize with getInstance(Game game) first.", exception);
            throw exception;
        }
    }

    public void ShowTestScreen() {
        gameContext.createTestConfig();
        game.setScreen(screenFactory.getScreen(PLAY, gameContext));
    }

    /**
     * Switches to the target screen type.
     *
     * For screens that require a fresh instance (like PLAY), always create a new one.
     * For others, reuse a cached instance if available.
     */
    public void switchScreenTo(ScreenType screenType) {
        if (currentScreen != null && currentScreen.getScreenType() == screenType) {
            Logger.getInstance().debug(TAG, "Already on screen: " + screenType);
            return;
        }

        IGameScreen newScreen;
        if (screenType == PLAY) {
            // Always create a new instance for gameplay because each level requires a fresh PlayScreen.
            gameContext.createNextLevel();
            newScreen = (IGameScreen) screenFactory.getScreen(screenType, gameContext);
            Logger.getInstance().log(TAG, "Switching to new PLAY screen: " + newScreen);
        } else {
            if (cachedScreens.containsKey(screenType)) {
                newScreen = (IGameScreen) cachedScreens.get(screenType);
                Logger.getInstance().log(TAG, "Cached screen contained " + screenType + ".");
            } else {
                newScreen = (IGameScreen) screenFactory.getScreen(screenType, gameContext);
                cachedScreens.put(screenType, (Screen) newScreen);
                Logger.getInstance().log(TAG, "Cached screens did not contain " + screenType + " so a new instance was added.");
            }
        }
        currentScreen = newScreen;
        game.setScreen((Screen) newScreen);
    }

    public FishMinerGame getGame() {
        return game;
    }

    /**
     * Start a new game from scratch. This resets the GameContext and clears any cached PlayScreen.
     */
    public void clearCache() {
        // Remove any cached instance of the PLAY screen.
        cachedScreens.remove(ScreenType.PLAY);
    }

    /**
     * Starts the next level. Since a new level requires a fresh instance,
     * always create a new PlayScreen for the next level.
     */
    public void startNextLevel() {
        Logger.getInstance().log(TAG, "startNextLevel was called.");
        cachedScreens.remove(PLAY);
        switchScreenTo(PLAY);
    }

    /**
     * Returns an event listener for change screen events.
     */
    public IGameEventListener<ChangeScreenEvent> getChangeScreenListener() {
        return new IGameEventListener<ChangeScreenEvent>() {
            @Override
            public void onEvent(ChangeScreenEvent event) {
                if (event.isHandled()) return;
                switchScreenTo(event.getScreenType());
                event.setHandled();
            }
            @Override
            public Class<ChangeScreenEvent> getEventType() {
                return ChangeScreenEvent.class;
            }
        };
    }
}
