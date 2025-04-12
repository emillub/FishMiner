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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.FishMiner.domain.GameContext;
import com.github.FishMiner.domain.events.screenEvents.ChangeScreenEvent;
import com.github.FishMiner.domain.events.soundEvents.MusicEvent;
import com.github.FishMiner.domain.World;
import com.github.FishMiner.domain.eventBus.GameEventBus;
import com.github.FishMiner.domain.events.ecsEvents.HookInputEvent;
import com.github.FishMiner.domain.ports.in.IGameScreen;
import com.github.FishMiner.domain.managers.ScreenManager;
import com.github.FishMiner.ui.ports.out.ScreenType;
import com.github.FishMiner.ui.ports.in.IPlayer;

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

    public PlayScreen(GameContext gameContext) {
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
        super.stage.draw();
        gameContext.update(delta);
        updateScoreTimeOverlay(batch);
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
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameEventBus.getInstance().post(new ChangeScreenEvent(ScreenType.MENU));
            }
        });

        pauseOverlay.add(resumeButton).width(200).height(60).pad(20).row();
        pauseOverlay.add(quitButton).width(200).height(60).pad(10);

        stage.addActor(pauseButton);
        stage.addActor(pauseOverlay);
    }


    /**
     * Updates the display for score and time remaining. Must be rendered after everything else.
     * If not rendered last, it will be rendered behind the background
     * @param batch spritebatch for this screen
     */
    private void updateScoreTimeOverlay(SpriteBatch batch) {
        batch.begin();
        font.draw(batch, "Time Left: " + Math.max(0, (int) world.getTimer()) + "s", 10, Gdx.graphics.getHeight() * 0.95f);
        font.draw(batch, "Score: " + (int) world.getScore() + "/" + world.getTargetScore(), 10, Gdx.graphics.getHeight() * 0.98f);
        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        font.dispose();
    }
}
