package com.github.FishMiner.domain.managers;

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
             screenFactory,   "screenFactory",
                    game,            "fishMinerGame",
                    gameContext,     "gameContext"
        );
        this.gameContext = gameContext;
        this.screenFactory = screenFactory;
        this.game = game;
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
            // Always create a new instance for gameplay, as each level needs a new PlayScreen.
            newScreen = (IGameScreen) screenFactory.getScreen(screenType, gameContext);
        } else {
            if (cachedScreens.containsKey(screenType)) {
                newScreen = (IGameScreen) cachedScreens.get(screenType);
            } else {
                newScreen = (IGameScreen) screenFactory.getScreen(screenType, gameContext);
                cachedScreens.put(screenType, (Screen) newScreen);
            }
        }
        currentScreen = newScreen;
        game.setScreen((Screen) newScreen);
    }

    /**
     * Replaces the Screen in cache if that screen is not the currentScreen,
     * and registers that Screen to the GameEventBus
     * @param type ScreenType name of a screen
     */
    public void prepareNewScreen(ScreenType type) {
        if (cachedScreens.containsKey(type)) {
            Screen cached = cachedScreens.get(type);

            if (currentScreen != null && cached == currentScreen) {
                Logger.getInstance().debug(TAG, "Cannot prepare a new instance for " + type + " because it is currently active.");
                return;
            }

            IGameScreen newScreen = (IGameScreen) screenFactory.getScreen(type, gameContext);
            cachedScreens.put(type, (Screen) newScreen);

            Logger.getInstance().log(TAG, "New instance for " + type + " prepared and cached.");
        }
        else { // if cachedScreen does not contain screenType
            IGameScreen newScreen = (IGameScreen) screenFactory.getScreen(type, gameContext);
            cachedScreens.put(type, (Screen) newScreen);
            Logger.getInstance().log(TAG, "Screen " + type + " was not cached. New instance created and cached.");
        }
    }

    public FishMinerGame getGame() {
        return game;
    }

    /**
     * Start a new game from scratch. This resets the GameContext
     * and clears any cached PlayScreen.
     */
    public void startNewGame() {
        gameContext.resetGame();
        gameContext.getEngine().update(0f); // ✅ Force update to re-sync entities
        // Remove any cached instance of the PLAY screen.
        cachedScreens.remove(ScreenType.PLAY);
        switchScreenTo(PLAY);
    }

    /**
     * Starts the next level. Since a new level requires a fresh instance,
     * always create a new PlayScreen for the next level.
     */
    public void startNextLevel() {
        gameContext.createNextLevel();
        cachedScreens.remove(PLAY);
        switchScreenTo(PLAY);
    }

    /**
     * Returns an event listener for change screen events.
     */
    public IGameEventListener<ChangeScreenEvent> getChangeScreenEvent() {
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

    /**
     * Returns an event listener for prepare new screen events.
     */
    public IGameEventListener<PrepareScreenEvent> getPrepareScreenEvent() {
        return new IGameEventListener<PrepareScreenEvent>() {
            @Override
            public void onEvent(PrepareScreenEvent event) {
                if (event.isHandled()) return;
                prepareNewScreen(event.getScreenType());
                event.setHandled();
            }
            @Override
            public Class<PrepareScreenEvent> getEventType() {
                return PrepareScreenEvent.class;
            }
        };
    }

    public IGameScreen getCurrentScreen() {
        return currentScreen;
    }

}
