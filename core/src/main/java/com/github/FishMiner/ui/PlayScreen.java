package com.github.FishMiner.ui;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.PositionComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.entityFactories.IGameEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.impl.BasicGameEntityFactory;
import com.github.FishMiner.domain.level.LevelConfig;
import com.github.FishMiner.domain.level.LevelConfigFactory;
import com.github.FishMiner.domain.ecs.systems.AnimationSystem;
import com.github.FishMiner.domain.ecs.systems.CollisionSystem;
import com.github.FishMiner.domain.ecs.systems.HookSystem;
import com.github.FishMiner.domain.ecs.systems.MovementSystem;
import com.github.FishMiner.domain.ecs.systems.PhysicalSystem;
import com.github.FishMiner.domain.ecs.systems.RenderingSystem;
import com.github.FishMiner.domain.ecs.systems.RotationSystem;
import com.github.FishMiner.domain.ecs.systems.SpawningQueueSystem;
import com.github.FishMiner.domain.World;
import com.github.FishMiner.domain.events.GameEventBus;
import com.github.FishMiner.domain.events.impl.FireInputEvent;
import com.github.FishMiner.domain.states.WorldState;
import com.github.FishMiner.ui.controller.InputController;


/**
 * PlayScreen handles gameplay, including ECS initialization, rendering, and input.
 * It also provides a full-width control window with a Menu button to open an overlay.
 */
public class PlayScreen extends AbstractScreen {
    private PooledEngine engine;
    private SpriteBatch batch;
    private InputController controller;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;


    private World world;
    private int levelNumber = 4; // Start at level 1

    @Override
    public void show() {
        super.show();

        // Initialize ECS engine, input controller, and sprite batch
        engine = Configuration.getInstance().getEngine();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(com.badlogic.gdx.graphics.Color.BLACK);


        // Create and add entities
        IGameEntityFactory entityFactory = new BasicGameEntityFactory(); // Abstract factory pattern
        engine.addEntity(entityFactory.createHook());  // Add hook to the scene

        // Add ECS systems
        System.out.println("Adding systems...");
        RotationSystem rotationSystem = new RotationSystem();
        rotationSystem.setHookPosition(
            (int) (Configuration.getInstance().getScreenWidth() / 2),
            Configuration.getInstance().getOceanHeight()
        );
        engine.addSystem(rotationSystem);
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new AnimationSystem());
        engine.addSystem(new MovementSystem());
        engine.addSystem(new RenderingSystem(batch));
        engine.addSystem(new HookSystem());
        engine.addSystem(new PhysicalSystem());

        SpawningQueueSystem spawningSystem = new SpawningQueueSystem();
        engine.addSystem(spawningSystem);

        System.out.println("Systems added");

        // Create world and configure level
        world = new World(engine);
        LevelConfig config = LevelConfigFactory.generateLevel(levelNumber, (int) world.getScore()); // Pass previous score
        world.createLevel(config);

        // Setup ECS systems
        SpawningQueueSystem spawningQueueSystem = engine.getSystem(SpawningQueueSystem.class);
        if (spawningQueueSystem != null) {
            spawningQueueSystem.configureFromLevel(config); // Configure spawn system with current level settings
        }

        // Input system for interacting with the hook
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.SPACE) {
                    // Retrieve the hook entity and post input event
                    ImmutableArray<Entity> hooks = engine.getEntitiesFor(Family.all(HookComponent.class, PositionComponent.class, StateComponent.class).get());
                    if (hooks.size() > 0) {
                        Entity hook = hooks.first();
                        GameEventBus.getInstance().post(new FireInputEvent(hook));
                        System.out.println("FireInputEvent posted.");
                    }
                    return true;
                }
                return false;
            }
        });

        Gdx.input.setInputProcessor(stage);
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

        // If the game is lost and the overlay hasn't been added yet, create it
        if (world.getTimer() <= 0) {
            if (world.getState() == WorldState.WON) {
                // Proceed to next level for the next game if within level limit
                if (levelNumber < 20) {
                    levelNumber++;
                    System.out.println("Advancing to level " + levelNumber);
                    LevelConfig nextLevelConfig = LevelConfigFactory.generateLevel(levelNumber, (int) world.getScore());
                    world.createLevel(nextLevelConfig);
                } else {
                    // All levels completed – trigger game completion flow
                    System.out.println("Congratulations! All levels completed!");
                    // For example, transition to a game-complete screen.
                }
            } else if (world.getState() == WorldState.LOST) {
                // Handle game over logic here (e.g., restart level or show game-over overlay)
                System.out.println("Game Over. Try again!");
                // Optionally, you could recreate the same level or go to a menu.
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
            Configuration.getInstance().getScreenHeight() - Configuration.getInstance().getOceanHeight()
        );

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
}
