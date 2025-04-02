package com.github.FishMiner.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Pool;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.FishMinerGame;
import com.github.FishMiner.domain.ecs.components.HookComponent;
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
import com.github.FishMiner.domain.states.WorldState;
import com.github.FishMiner.ui.controller.ScreenManager;


/**
 * PlayScreen handles gameplay, including ECS initialization, rendering, and input.
 * It also provides a full-width control window with a Menu button to open an overlay.
 */
public class PlayScreen extends AbstractScreen {
    private PooledEngine engine;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private World world;
    private int levelNumber;
    private float previousScore;

    public PlayScreen(int levelNumber, float previousScore) {
        this.levelNumber = levelNumber;
        this.previousScore = previousScore;
    }

    @Override
    public void show() {
        super.show();
        engine = Configuration.getInstance().getEngine();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(com.badlogic.gdx.graphics.Color.BLACK);

        PlayerFactory.addNewPlayerCharacterTo(engine,
            (int) (Configuration.getInstance().getScreenWidth() / 2),
            Configuration.getInstance().getOceanHeight()
        );

        // add systems to engine
        addSystemTo(engine);

        // only init World after all systems and stuff has been added to engine:)
        world = new World(engine);
        LevelConfig config = LevelConfigFactory.generateLevel(levelNumber, (int) world.getScore());
        world.createLevel(config);
        GameEventBus.getInstance().register(world);

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

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        cam.update();
        float score = world.getScore();


        batch.begin();
        font.draw(batch, "Time Left: " + Math.max(0, (int) world.getTimer()) + "s", 10, Gdx.graphics.getHeight() - 10);
        font.draw(batch, "Score: " + (int) score + "/" + world.getTargetScore(), 10, Gdx.graphics.getHeight() - 40);
        batch.end();

        // If the game is lost and the overlay hasn't been added yet, create it
        if (world.getTimer() <= 0) {
            if (world.getState() == WorldState.WON) {
                // Proceed to next level for the next game if within level limit
                System.out.println("Advancing to level " + levelNumber);
                ScreenManager.getInstance().showLevelCompleteScreen(levelNumber, world.getScore());
                return;
            } else if (world.getState() == WorldState.LOST) {
                System.out.println("Game Over. Try again!");
                ScreenManager.getInstance().showLevelLostScreen();
                return;
            }
    }

        // Update ECS systems and render
        stage.act(delta);
        engine.update(delta);
        stage.draw();
        world.update(delta);
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        font.dispose();
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

        FishingSystem fishSystem =  new FishingSystem();
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
}
