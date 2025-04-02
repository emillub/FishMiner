package com.github.FishMiner.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Pool;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.FishMinerGame;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.entityFactories.playerFactory.PlayerFactory;
import com.github.FishMiner.domain.ecs.systems.FishingSystem;
import com.github.FishMiner.domain.ecs.systems.HookInputSystem;
import com.github.FishMiner.domain.ecs.systems.test.DebugRenderingSystem;
import com.github.FishMiner.domain.level.LevelConfig;
import com.github.FishMiner.domain.level.LevelConfigFactory;
import com.github.FishMiner.domain.ecs.systems.AnimationSystem;
import com.github.FishMiner.domain.ecs.systems.CollisionSystem;
import com.github.FishMiner.domain.ecs.systems.HookSystem;
import com.github.FishMiner.domain.ecs.systems.MovementSystem;
import com.github.FishMiner.domain.ecs.systems.PhysicalSystem;
import com.github.FishMiner.domain.ecs.systems.RenderingSystem;
import com.github.FishMiner.domain.ecs.systems.SpawningQueueSystem;
import com.github.FishMiner.domain.World;
import com.github.FishMiner.domain.events.GameEventBus;
import com.github.FishMiner.domain.events.impl.FireInputEvent;
import com.github.FishMiner.domain.states.WorldState;
import com.github.FishMiner.ui.controller.ScreenManager;


/**
 * PlayScreen handles gameplay, including ECS initialization, rendering, and input.
 * It also provides a full-width control window with a Menu button to open an overlay.
 */
public class PlayScreen extends AbstractScreen {

    private static final float OVERLAY_ALPHA = 0.6f;
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 60;

    // Core
    private PooledEngine engine;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private World world;

    // Level info
    private final int levelNumber;
    private final float previousScore;

    // UI
    private TextButton pauseButton;
    private Table pauseOverlay;
    private boolean overlayVisible = false;

    public PlayScreen(int levelNumber, float previousScore) {
        this.levelNumber = levelNumber;
        this.previousScore = previousScore;
    }

    @Override
    public void show() {
        super.show();
        initializeCore();
        setupWorld();
        setupInput();
        setupPauseUI();
        FishMinerGame.playGameMusic();
    }

    private void initializeCore() {
        engine = Configuration.getInstance().getEngine();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.BLACK);

        PlayerFactory.addNewPlayerCharacterTo(engine,
            (int) (Configuration.getInstance().getScreenWidth() / 2),
            Configuration.getInstance().getOceanHeight()
        );

        addSystems(engine);
    }

    private void setupWorld() {
        world = new World(engine);
        LevelConfig config = LevelConfigFactory.generateLevel(levelNumber, (int) world.getScore());
        world.createLevel(config);
    }

    private void setupInput() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.SPACE && !world.isPaused()) {
                    ImmutableArray<Entity> hooks = engine.getEntitiesFor(Family.all(HookComponent.class, TransformComponent.class, StateComponent.class).get());
                    if (hooks.size() > 0) {
                        Entity hook = hooks.first();
                        GameEventBus.getInstance().post(new FireInputEvent(hook));
                    }
                    return true;
                }
                return false;
            }
        });
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void setupPauseUI() {
        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

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
        redStyle.font = skin.getFont("default"); // or "default" if your skin uses that
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
                ScreenManager.getInstance().showMenu();
            }
        });

        pauseOverlay.add(resumeButton).width(200).height(60).pad(20).row();
        pauseOverlay.add(quitButton).width(200).height(60).pad(10);

        stage.addActor(pauseButton);
        stage.addActor(pauseOverlay);
    }




    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.update(delta);
        drawBackground();

        batch.begin();
        font.draw(batch, "Time Left: " + Math.max(0, (int) world.getTimer()) + "s", 10, Gdx.graphics.getHeight() - 10);
        font.draw(batch, "Score: " + (int) world.getScore() + "/" + world.getTargetScore(), 10, Gdx.graphics.getHeight() - 40);
        batch.end();

        if (world.getState() == WorldState.WON) {
            ScreenManager.getInstance().showLevelCompleteScreen(levelNumber, world.getScore());
            return;
        } else if (world.getState() == WorldState.LOST) {
            ScreenManager.getInstance().showLevelLostScreen();
            return;
        }

        stage.act(delta);
        if (!world.isPaused()) {
            engine.update(delta);
        }
        stage.draw();
    }

    private void drawBackground() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.5f, 0.8f, 1f, 1f);
        shapeRenderer.rect(
            0,
            Configuration.getInstance().getOceanHeight(),
            Configuration.getInstance().getScreenWidth(),
            Configuration.getInstance().getScreenHeight() - Configuration.getInstance().getOceanHeight()
        );

        int levels = Configuration.getInstance().getDepthLevels();
        int levelHeight = Configuration.getInstance().getOceanHeight() / levels;
        for (int i = 0; i < levels; i++) {
            float shade = 0.2f + (i * 0.15f);
            shapeRenderer.setColor(0f, 0f, shade, 1f);
            shapeRenderer.rect(0, i * levelHeight, Configuration.getInstance().getScreenWidth(), levelHeight);
        }

        shapeRenderer.end();
    }

    private void addSystems(Engine engine) {
        engine.addSystem(new AnimationSystem());
        engine.addSystem(new MovementSystem());
        engine.addSystem(new RenderingSystem(batch));
        engine.addSystem(new HookSystem());
        engine.addSystem(new PhysicalSystem());
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new SpawningQueueSystem());

        FishingSystem fishSystem = new FishingSystem();
        engine.addSystem(fishSystem);
        GameEventBus.getInstance().register(fishSystem);

        HookInputSystem hookInputSystem = new HookInputSystem();
        engine.addSystem(hookInputSystem);
        GameEventBus.getInstance().register(hookInputSystem);

        if (Configuration.getInstance().isDebugMode()) {
            engine.addSystem(new DebugRenderingSystem());
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        font.dispose();
    }
}
