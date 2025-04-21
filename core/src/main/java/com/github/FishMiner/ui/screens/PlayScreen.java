package com.github.FishMiner.ui.screens;

import static com.github.FishMiner.domain.events.soundEvents.MusicEvent.MusicCommand.PLAY_GAME;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Scaling;
import com.github.FishMiner.common.Assets;
import com.github.FishMiner.common.Configuration;
import com.github.FishMiner.data.ScoreEntry;
import com.github.FishMiner.domain.events.screenEvents.ChangeScreenEvent;
import com.github.FishMiner.domain.events.soundEvents.MusicEvent;
import com.github.FishMiner.domain.World;
import com.github.FishMiner.domain.GameEventBus;
import com.github.FishMiner.domain.events.ecsEvents.HookInputEvent;
import com.github.FishMiner.domain.events.uiEvents.DisplayScoreValueEvent;
import com.github.FishMiner.domain.ports.in.IGameEventListener;
import com.github.FishMiner.domain.ports.in.IGameScreen;
import com.github.FishMiner.domain.session.UserSession;
import com.github.FishMiner.ui.events.data.LeaderboardPostRequestEvent;
import com.github.FishMiner.ui.factories.ButtonFactory;
import com.github.FishMiner.ui.ports.out.IGameContext;
import com.github.FishMiner.ui.ports.out.ScreenType;

import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset;

/**
 * PlayScreen handles gameplay, including ECS initialization, rendering, and input.
 * It also provides a full-width control window with a Menu button to open an overlay.
 */
public class PlayScreen extends AbstractScreen implements IGameScreen {
    private static final String TAG = "PlayScreen";
    private static final float OVERLAY_ALPHA = 0.6f;
    private World world;
    private Table pauseOverlay;
    private boolean scoreSubmitted = false;
    private Table overlayTable;
    private Label levelLabel;
    private Label timeLabel;
    private Label scoreLabel;

    public PlayScreen(IGameContext gameContext) {
        super(gameContext);
        super.screenType = ScreenType.PLAY;
        world = gameContext.getWorld();
    }


    @Override
    public void show() {
        super.show();
        if (world.isPaused()) {
            world.resume();
        }
        setupInput();
        setupPauseUI();
        setupScoreTimeOverlay();
        GameEventBus.getInstance().register(displayScoreListener);
        GameEventBus.getInstance().post(new MusicEvent(PLAY_GAME));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        cam.update();

        gameContext.update(delta);
        if (world.isPaused()) {
            super.drawBackground();
        }

        super.stage.act(delta);
        updateScoreTimeOverlay();

        super.stage.draw();
    }

    private void setupScoreTimeOverlay() {
        overlayTable = new Table();
        overlayTable.top().left();
        overlayTable.setFillParent(true);

        levelLabel = new Label("", skin);
        timeLabel = new Label("", skin);
        scoreLabel = new Label("", skin);

        levelLabel.setFontScale(Configuration.getInstance().getSmallFontScale());
        timeLabel.setFontScale(Configuration.getInstance().getSmallFontScale());
        scoreLabel.setFontScale(Configuration.getInstance().getSmallFontScale());
        levelLabel.setColor(Assets.DARK_BROWN);
        timeLabel.setColor(Assets.DARK_BROWN);
        scoreLabel.setColor(Assets.DARK_BROWN);

        overlayTable.add(levelLabel).pad(Configuration.getInstance().getSmallPadding()).left().row();
        overlayTable.add(timeLabel).pad(Configuration.getInstance().getSmallPadding()).left().row();
        overlayTable.add(scoreLabel).pad(Configuration.getInstance().getSmallPadding()).left();

        stage.addActor(overlayTable);
    }

    private void setupInput() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (pointer > 0) return false;

                Vector2 stageCoords = stage.screenToStageCoordinates(new Vector2(screenX, screenY));
                if (stage.hit(stageCoords.x, stageCoords.y, true) == null) {
                    GameEventBus.getInstance().post(new HookInputEvent(gameContext.getPlayer()));
                }
                return false;
            }
        });
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void setupPauseUI() {
        // === Pause Overlay ===
        pauseOverlay = new Table();
        pauseOverlay.setFillParent(true);
        pauseOverlay.setVisible(false);

        // Create a faded white background
        Table background = new Table();
        background.setFillParent(true);
        background.setColor(Color.WHITE); // Set the color to white
        background.getColor().a = OVERLAY_ALPHA; // Set transparency

        stage.addActor(background); // Add the background first so it appears behind other elements

        ImageButton pauseButton = ButtonFactory.createToggleButton(world.isPaused(), Assets.ButtonEnum.PAUSE,
                Assets.ButtonEnum.PLAY,
                () -> {
                world.togglePause();
                    pauseOverlay.setVisible(world.isPaused());
                });
        float buttonWidth = Configuration.getInstance().getMediumIconWidth();
        pauseButton.getImage().setScaling(Scaling.fit);
        pauseButton.setPosition(Gdx.graphics.getWidth() - buttonWidth - Configuration.getInstance().getSmallPadding(),
                Gdx.graphics.getHeight() - buttonWidth - Configuration.getInstance().getSmallPadding());
        pauseButton.setSize(buttonWidth, buttonWidth);

        ImageButton soundButton = ButtonFactory.createToggleButton(Configuration.getInstance().isMusicEnabled(),
                Assets.ButtonEnum.MUTED, Assets.ButtonEnum.SOUND,
                () -> {
                    GameEventBus.getInstance().post(new MusicEvent(MusicEvent.MusicCommand.TOGGLE_MUTE));
                });
        soundButton.setPosition(
                Gdx.graphics.getWidth() - buttonWidth * 2 - Configuration.getInstance().getSmallPadding()
                        - Configuration.getInstance().getSmallPadding(),
                Gdx.graphics.getHeight() - buttonWidth - Configuration.getInstance().getSmallPadding());
        soundButton.setSize(buttonWidth, buttonWidth);

        stage.addActor(pauseOverlay); // Add the pause overlay after the background

        Table pauseButtonTable = new Table();
        pauseButtonTable.top().right();
        pauseButtonTable.setFillParent(true);
        pauseButtonTable.add(pauseButton).size(buttonWidth, buttonWidth)
                .padTop(Configuration.getInstance().getSmallPadding())
                .padRight(Configuration.getInstance().getSmallPadding());
        pauseButtonTable.add(soundButton).size(buttonWidth, buttonWidth)
                .padTop(Configuration.getInstance().getSmallPadding())
                .padRight(Configuration.getInstance().getSmallPadding());
        stage.addActor(pauseButtonTable);

        // Quit button
        TextButton continueButton = ButtonFactory.createTextButton("Continue", ButtonFactory.ButtonSize.MEDIUM, () -> {
            world.togglePause();
            pauseButton.setChecked(world.isPaused());
            pauseOverlay.setVisible(world.isPaused());
        });

        TextButton quitButton = ButtonFactory.createTextButton("Quit", ButtonFactory.ButtonSize.MEDIUM, () -> {
            float score = gameContext.getWorld().getScore();
            if (UserSession.isLoggedIn() && score > 0) {
                ScoreEntry entry = new ScoreEntry(UserSession.getCurrentUserEmail(), (int) score);
                GameEventBus.getInstance().post(new LeaderboardPostRequestEvent(entry));
            }

            GameEventBus.getInstance().post(new ChangeScreenEvent(ScreenType.MENU));
        });

        pauseOverlay.add(continueButton).size(continueButton.getWidth(),
                continueButton.getHeight()).padBottom(Configuration.getInstance().getSmallPadding()).row();

        pauseOverlay.add(quitButton).size(quitButton.getWidth(),
                quitButton.getHeight());

        stage.addActor(pauseOverlay);
    }

    private final IGameEventListener<DisplayScoreValueEvent> displayScoreListener =
        new IGameEventListener<DisplayScoreValueEvent>() {
            @Override
            public void onEvent(DisplayScoreValueEvent event) {
                float value = event.value;

                // Create the label style
                Label.LabelStyle style = new Label.LabelStyle();
                style.font = skin.getFont("default");
                style.fontColor = value >= 0 ? new Color(0f, 0.6f, 0f, 1f) : new Color(0.7f, 0f, 0f, 1f);

                String prefix = value >= 0 ? "+" : "-";
                String displayValue = prefix + Math.abs((int) value);

                Label scoreLabel = new Label(displayValue, style);
                    scoreLabel.setFontScale(Configuration.getInstance().getSmallFontScale());
                Vector2 screenCoords = stage.getViewport().project(new Vector2(event.x, event.y));
                Vector2 stageCoords = stage.screenToStageCoordinates(screenCoords);
                scoreLabel.setPosition(stageCoords.x + 100, stageCoords.y + 450);
                scoreLabel.addAction(Actions.sequence(
                    Actions.parallel(
                        Actions.moveBy(0, 50, 1f),
                        Actions.fadeOut(1f)
                    ),
                    Actions.removeActor()
                ));

                stage.addActor(scoreLabel);
                event.setHandled();
            }


            @Override
            public Class<DisplayScoreValueEvent> getEventType() {
                return DisplayScoreValueEvent.class;
            }
        };


    private void updateScoreTimeOverlay() {
        levelLabel.setText("Level: " + world.getLevel());
        timeLabel.setText("Time Left: " + Math.max(0, (int) world.getTimer()) + "s");
        scoreLabel.setText("Score: " + (int) world.getScore() + "/" + world.getTargetScore());
    }

    private void submitFinalScoreAndGoToLeaderboard() {
        if (scoreSubmitted) return; // Avoid resubmitting multiple times
        scoreSubmitted = true;

        int score = (int) world.getScore();

        if (UserSession.isLoggedIn()) {
            ScoreEntry entry = new ScoreEntry(UserSession.getCurrentUserEmail(), score);
            GameEventBus.getInstance().post(new LeaderboardPostRequestEvent(entry));
        }

        GameEventBus.getInstance().post(new ChangeScreenEvent(ScreenType.LEADERBOARD));
    }


    @Override
    public void dispose() {
        GameEventBus.getInstance().unregister(displayScoreListener);
        GameEventBus.getInstance().unregister(world);
        super.dispose();
        stage.clear();
        stage.dispose();
        batch.dispose();
        font.dispose();
        Assets.getInstance().dispose();
    }
}
