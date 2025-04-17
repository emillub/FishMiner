package com.github.FishMiner.ui.screens;

import static com.github.FishMiner.domain.events.soundEvents.MusicEvent.MusicCommand.PLAY_GAME;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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
import com.github.FishMiner.ui.ports.out.IGameContext;
import com.github.FishMiner.ui.ports.out.ScreenType;

/**
 * PlayScreen handles gameplay, including ECS initialization, rendering, and input.
 * It also provides a full-width control window with a Menu button to open an overlay.
 */
public class PlayScreen extends AbstractScreen implements IGameScreen {
    private static final String TAG = "PlayScreen";
    private static final float OVERLAY_ALPHA = 0.6f;
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 60;
    private World world;
    private TextButton pauseButton;
    private Table pauseOverlay;
    private boolean overlayVisible = false;
    private boolean scoreSubmitted = false;

    public PlayScreen(IGameContext gameContext) {
        super(gameContext);
        super.screenType = ScreenType.PLAY;
        world = gameContext.getWorld();
    }


    @Override
    public void show() {
        super.show();
        stage.clear();

        setupInput();
        setupPauseUI();
        GameEventBus.getInstance().register(displayScoreListener);
        GameEventBus.getInstance().post(new MusicEvent(PLAY_GAME));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        cam.update();

        if (!world.isPaused()) {
            gameContext.update(delta);
        }

        super.stage.act(delta);

        gameContext.update(delta);
        updateScoreTimeOverlay(batch);

        super.stage.draw();
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
        // === Pause Button (icon only) ===
        Texture pauseTexture = new Texture(Gdx.files.internal("pause-button.png"));
        TextureRegionDrawable pauseDrawable = new TextureRegionDrawable(new TextureRegion(pauseTexture));

        TextButton.TextButtonStyle pauseStyle = new TextButton.TextButtonStyle();
        pauseStyle.up = pauseDrawable;
        pauseStyle.down = pauseDrawable;
        pauseStyle.over = null;
        pauseStyle.font = new BitmapFont();

        pauseButton = new TextButton("", pauseStyle);
        pauseButton.setSize(48, 48);
        pauseButton.setPosition(Gdx.graphics.getWidth() - 60, Gdx.graphics.getHeight() - 60);
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                world.togglePause();
                overlayVisible = world.isPaused();
                pauseOverlay.setVisible(overlayVisible);
                pauseButton.setVisible(!overlayVisible);
            }
        });
        // === Pause Overlay ===
        pauseOverlay = new Table();
        pauseOverlay.setFillParent(true);
        pauseOverlay.setVisible(false);
        pauseOverlay.setBackground(skin.newDrawable("white", 0, 0, 0, 0.6f));
        pauseOverlay.center();

        // Red button background
        TextureRegionDrawable redDrawable = new TextureRegionDrawable(new TextureRegion(new Texture("Red-Button.png")));

        // Text button style for overlay
        TextButton.TextButtonStyle redStyle = new TextButton.TextButtonStyle();
        redStyle.up = redDrawable;
        redStyle.down = redDrawable;
        redStyle.font = super.skin.getFont("default");
        redStyle.fontColor = Color.WHITE;

        // Resume button
        TextButton resumeButton = new TextButton("Resume", redStyle);
        resumeButton.pad(10);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                world.togglePause();
                overlayVisible = false;
                pauseOverlay.setVisible(false);
                pauseButton.setVisible(true);
            }
        });

        // Quit button
        TextButton quitButton = new TextButton("Quit", redStyle);
        quitButton.pad(10);
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float score = gameContext.getWorld().getScore();

                if (UserSession.isLoggedIn() && score > 0) {
                    ScoreEntry entry = new ScoreEntry(UserSession.getCurrentUserEmail(), (int) score);
                    GameEventBus.getInstance().post(new LeaderboardPostRequestEvent(entry));
                }

                GameEventBus.getInstance().post(new ChangeScreenEvent(ScreenType.MENU));
            }
        });

        pauseOverlay.add(resumeButton).width(200).height(60).pad(20).row();
        pauseOverlay.add(quitButton).width(200).height(60).pad(10);

        stage.addActor(pauseButton);
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
                scoreLabel.setFontScale(1.5f);
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



    /**
     * Updates the display for score and time remaining. Must be rendered after everything else.
     * If not rendered last, it will be rendered behind the background
     * @param batch spritebatch for this screen
     */
    private void updateScoreTimeOverlay(SpriteBatch batch) {
        batch.begin();
        font.draw(batch, "Level: " + world.getLevel(), 10, Gdx.graphics.getHeight() * 0.92f);
        font.draw(batch, "Time Left: " + Math.max(0, (int) world.getTimer()) + "s", 10, Gdx.graphics.getHeight() * 0.95f);
        font.draw(batch, "Score: " + (int) world.getScore() + "/" + world.getTargetScore(), 10, Gdx.graphics.getHeight() * 0.98f);
        batch.end();
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
        super.dispose();
        batch.dispose();
        font.dispose();
    }
}
