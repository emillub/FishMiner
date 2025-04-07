package com.github.FishMiner.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.FishMinerGame;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.InventoryComponent;
import com.github.FishMiner.domain.ecs.components.PlayerComponent;
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
import com.github.FishMiner.domain.listeners.FishCaptureListener;
import com.github.FishMiner.domain.states.WorldState;
import com.github.FishMiner.ui.controller.ScreenManager;


/**
 * PlayScreen handles gameplay, including ECS initialization, rendering, and input.
 * It also provides a full-width control window with a Menu button to open an overlay.
 */
public class PlayScreen extends AbstractScreen {
    private Engine engine;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private World world;
    private int levelNumber;
    private InventoryComponent inventory;


    public PlayScreen(int levelNumber, InventoryComponent inventory) {
        this.levelNumber = levelNumber;
        this.inventory = (inventory != null) ? inventory : new InventoryComponent();
    }

    @Override
    public void show() {
        super.show();
        engine = Configuration.getInstance().getEngine();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        float scale = Configuration.getInstance().getUniformScale();
        font = new BitmapFont();
        font.getData().setScale(1.3f * scale);
        font.setColor(com.badlogic.gdx.graphics.Color.BLACK);

        PlayerFactory.addNewPlayerCharacterTo(engine,
            (int) (Configuration.getInstance().getScreenWidth() / 2),
            Configuration.getInstance().getOceanHeight(), inventory
        );


        // add systems to engine
        addSystemTo(engine);

        // only init World after all systems and stuff has been added to engine:)
        world = new World(engine);
        GameEventBus.getInstance().register(new FishCaptureListener(world));
        LevelConfig config = LevelConfigFactory.generateLevel(levelNumber, (int) inventory.money);
        world.createLevel(config, inventory.money);


        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.SPACE) {
                    // Retrieve the hook entity from the engine.
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

        FishMinerGame.playGameMusic();
        buildUI();

    }

    @Override
    protected void buildUI() {
        // Currently no UI widgets (HUD uses BitmapFont + SpriteBatch)
        // In future: Add pause button, HUD, controls etc. here
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.update(delta);

        drawBackground();

        batch.begin();
        font.draw(batch, "Time Left: " + Math.max(0, (int) world.getTimer()) + "s", 10, Gdx.graphics.getHeight() - 10);
        font.draw(batch, "Score: " + (int) world.getScore() + "/" + world.getTargetScore(), 10, Gdx.graphics.getHeight() - 60);
        batch.end();

        // If the game is lost and the overlay hasn't been added yet, create it
        if (world.getTimer() <= 0) {
            if (world.getState() == WorldState.WON) {
                // Proceed to next level for the next game if within level limit
                System.out.println("Advancing to level " + levelNumber);
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
                ScreenManager.getInstance().showLevelLostScreen();
                return;
            }
        }

        // Update ECS systems and render
        stage.act(delta);
        engine.update(delta);
        stage.draw();



    }

    private void drawBackground() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw the ocean background (light sky blue)
        shapeRenderer.setColor(0.5f, 0.8f, 1f, 1f);
        shapeRenderer.rect(
            0,
            Configuration.getInstance().getOceanHeight(),
            Configuration.getInstance().getScreenWidth(),
            Configuration.getInstance().getScreenHeight() - Configuration.getInstance().getOceanHeight());

        // Draw depth levels as darker shades
        int levels = Configuration.getInstance().getDepthLevels();
        int levelHeight = Configuration.getInstance().getOceanHeight() / levels;
        for (int i = 0; i < levels; i++) {
            float shade = 0.2f + (i * 0.15f);
            shapeRenderer.setColor(0f, 0f, shade, 1f);
            shapeRenderer.rect(
                0,
                i * levelHeight,
                Configuration.getInstance().getScreenWidth(),
                levelHeight
            );
        }
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        font.dispose();
    }

    private void addSystemTo(Engine engine) {
        // Add ECS systems
        engine.addSystem(new AnimationSystem());
        engine.addSystem(new MovementSystem());
        engine.addSystem(new RenderingSystem(batch));
        engine.addSystem(new HookSystem());
        engine.addSystem(new PhysicalSystem());
        engine.addSystem(new CollisionSystem());

        SpawningQueueSystem spawningSystem = new SpawningQueueSystem();
        engine.addSystem(spawningSystem);

        FishingSystem fishSystem = new FishingSystem();
        engine.addSystem(fishSystem);
        GameEventBus.getInstance().register(fishSystem);

        HookInputSystem hookInputSystem = new HookInputSystem();
        engine.addSystem(hookInputSystem);
        GameEventBus.getInstance().register(hookInputSystem);

        if (Configuration.getInstance().isDebugMode()) {
            // toggle Debug Mode in Configuration
            engine.addSystem(new DebugRenderingSystem());
        }
    }
}
