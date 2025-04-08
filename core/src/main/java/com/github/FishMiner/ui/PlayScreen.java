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
import com.github.FishMiner.Logger;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.InventoryComponent;
import com.github.FishMiner.domain.ecs.components.PlayerComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.entityFactories.playerFactory.PlayerFactory;
import com.github.FishMiner.domain.ecs.systems.BackgroundRenderSystem;
import com.github.FishMiner.domain.ecs.systems.FishingSystem;
import com.github.FishMiner.domain.ecs.systems.HookInputSystem;
import com.github.FishMiner.domain.ecs.systems.RotationSystem;
import com.github.FishMiner.domain.ecs.systems.ScoreSystem;
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
import com.github.FishMiner.domain.listeners.FishCaptureListener;
import com.github.FishMiner.domain.states.WorldState;
import com.github.FishMiner.ui.controller.ScreenManager;


/**
 * PlayScreen handles gameplay, including ECS initialization, rendering, and input.
 * It also provides a full-width control window with a Menu button to open an overlay.
 */
public class PlayScreen extends AbstractScreen {
    private static final String TAG = "PlayScreen";

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
    private int levelNumber;
    private InventoryComponent inventory;

    // UI
    private TextButton pauseButton;
    private Table pauseOverlay;
    private boolean overlayVisible = false;



    public PlayScreen(int levelNumber, InventoryComponent inventory) {
        this.levelNumber = levelNumber;
        this.inventory = (inventory != null) ? inventory : new InventoryComponent();
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
            Configuration.getInstance().getOceanHeight(), inventory
        );

        addSystemTo(engine);
    }

    private void setupWorld() {
        world = new World(engine);
        GameEventBus.getInstance().register(new FishCaptureListener(world));
        LevelConfig config = LevelConfigFactory.generateLevel(levelNumber, (int) inventory.money);
        Float customTimer = (levelNumber >= 50) ? 15f : null;
        world.createLevel(config, inventory.money, customTimer);
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
        cam.update();

        // If the game is lost and the overlay hasn't been added yet, create it
        if (world.getTimer() <= 0) {
            if (world.getState() == WorldState.WON) {
                Logger.getInstance().log(TAG, "Advancing to level " + levelNumber);
                ImmutableArray<Entity> players = engine.getEntitiesFor(Family.all(PlayerComponent.class).get());
                if (players.size() > 0) {
                    Entity player = players.first();
                    inventory = player.getComponent(InventoryComponent.class);
                    //testing
                    if (inventory == null) {
                        System.err.println("Player has no InventoryComponent!");
                        return; // prevent crash
                    }

                    ScreenManager.getInstance().showLevelCompleteScreen(levelNumber, inventory);
                    return;
                }

            } else if (world.getState() == WorldState.LOST) {
                System.out.println("Game Over. Try again!");

                if (inventory != null){
                    inventory.money = 0;
                }
                Logger.getInstance().debug(TAG, "Advancing to level " + levelNumber);
                ScreenManager.getInstance().showLevelCompleteScreen(levelNumber, inventory);
                return;
            } else if (world.getState() == WorldState.LOST) {
                Logger.getInstance().debug(TAG, "Game over. Level reached: " + levelNumber);
                ScreenManager.getInstance().showLevelLostScreen();
                return;
            }
        }

        stage.act(delta);
        if (!world.isPaused()) {
            engine.update(delta);
        }
        stage.draw();
        world.update(delta);
        updateScoreTimeOverlay(batch);
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

    private void addSystemTo(Engine engine) {
        engine.addSystem(new BackgroundRenderSystem(shapeRenderer));
        engine.addSystem(new AnimationSystem());
        RenderingSystem renderingSystem = new RenderingSystem(batch);
        cam = renderingSystem.getCam();
        engine.addSystem(renderingSystem);
        engine.addSystem(new MovementSystem());
        engine.addSystem(new HookSystem());
        engine.addSystem(new PhysicalSystem());
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new SpawningQueueSystem());
        engine.addSystem(new RotationSystem());

        // Registrer systems that are also listeners
        GameEventBus eventBus = GameEventBus.getInstance();

        ScoreSystem scoreSystem = new ScoreSystem();
        engine.addSystem(scoreSystem);
        eventBus.register(scoreSystem);

        FishingSystem fishSystem = new FishingSystem();
        engine.addSystem(fishSystem);
        eventBus.register(fishSystem);

        HookInputSystem hookInputSystem = new HookInputSystem();
        engine.addSystem(hookInputSystem);
        eventBus.register(hookInputSystem);

        if (Configuration.getInstance().isDebugMode()) {
            // toggle Debug Mode in Configuration
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
