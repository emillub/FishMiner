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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.entityFactories.FishTypes;
import com.github.FishMiner.domain.ecs.entityFactories.IGameEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.impl.BasicGameEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.impl.LevelFactory;
import com.github.FishMiner.domain.ecs.level.LevelConfig;
import com.github.FishMiner.domain.ecs.level.LevelConfigFactory;
import com.github.FishMiner.domain.ecs.systems.AnimationSystem;
import com.github.FishMiner.domain.ecs.systems.CollisionSystem;
import com.github.FishMiner.domain.ecs.systems.FishSystem;
import com.github.FishMiner.domain.ecs.systems.HookInputSystem;
import com.github.FishMiner.domain.ecs.systems.HookSystem;
import com.github.FishMiner.domain.ecs.systems.MovementSystem;
import com.github.FishMiner.domain.ecs.systems.PhysicalSystem;
import com.github.FishMiner.domain.ecs.systems.RenderingSystem;
import com.github.FishMiner.domain.ecs.systems.RotationSystem;
import com.github.FishMiner.domain.ecs.systems.SpawningQueueSystem;
import com.github.FishMiner.domain.ecs.util.World;
import com.github.FishMiner.domain.ecs.systems.test.DebugRenderingSystem;
import com.github.FishMiner.domain.events.GameEventBus;
import com.github.FishMiner.domain.events.impl.FireInputEvent;
import com.github.FishMiner.ui.controller.InputController;

import java.util.LinkedList;


/**
 * PlayScreen handles gameplay, including ECS initialization, rendering, and input.
 * It also provides a full-width control window with a Menu button to open an overlay.
 */
public class PlayScreen extends AbstractScreen {
    private Engine engine;
    private SpriteBatch batch;
    //private InputController controller;
    private ShapeRenderer shapeRenderer;


    @Override
    public void show() {
        super.show();

        // Initialize ECS engine, input controller, and sprite batch
        engine = Configuration.getInstance().getEngine();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // Create and add entities
        IGameEntityFactory entityFactory = new BasicGameEntityFactory(engine);

        engine.addEntity(entityFactory.createHook());


        // Add ECS systems
        System.out.println("adding systems");
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

        System.out.println("added systems");

        World world = new World(engine);

        int levelNumber = 5;
        FishSystem fishSystem =  new FishSystem();
        engine.addSystem(fishSystem);
        GameEventBus.getInstance().register(fishSystem);

        HookInputSystem hookInputSystem = new HookInputSystem();
        engine.addSystem(hookInputSystem);
        GameEventBus.getInstance().register(hookInputSystem);

        if (Configuration.getInstance().isDebugMode()) {
            // toggle Debug Mode in Configuration
            engine.addSystem(new DebugRenderingSystem());
        }

        LevelConfig config = LevelConfigFactory.generateLevel(levelNumber);
        world.createLevel(config);

        // 🖨️ DEBUG PRINT - remove later if needed
        //System.out.println("==== DEBUG: LEVEL INFO ====");
        //System.out.println("Level: " + levelNumber);
        //System.out.println("Target Score: " + config.getTargetScore());
        //System.out.println("Spawn Interval: " + config.getSpawnInterval());
        //System.out.println("Total Fish to Spawn: " + config.getTotalFishToSpawn());
        //System.out.println("Fish Spawn Probabilities:");
        //for (Map.Entry<FishTypes, Float> entry : config.getSpawnChances().entrySet()) {
        //    System.out.printf(" - %s: %.2f%%\n", entry.getKey().name(), entry.getValue() * 100);
        //}
        //System.out.println("===========================");

        // create fish for this level

        LinkedList<Entity> fishForLevel = prepareFishForLevel(entityFactory);
        LevelFactory levelFactory = new LevelFactory(engine);
        Entity levelEntity = levelFactory.createEntity(fishForLevel, 6f);
        engine.addEntity(levelEntity);


        //spawningSystem.configureFromLevel(config);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.SPACE) {
                    // Retrieve the hook entity from the engine.
                    ImmutableArray<Entity> hooks = engine.getEntitiesFor(Family.all(HookComponent.class, TransformComponent.class, StateComponent.class).get());
                    if (hooks.size() > 0) {
                        Entity hook = hooks.first();
                        // Post the event to the GameEventBus.
                        //GameEventBus.getInstance().post(new FireInputEvent(hook));
                        GameEventBus.getInstance().post(new FireInputEvent(hook));
                        System.out.println("FireInputEvent posted.");
                    }
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.5f, 0.8f, 1f, 1f); // light sky blue
        shapeRenderer.rect(0, Configuration.getInstance().getOceanHeight(),
            Configuration.getInstance().getScreenWidth(),
            Configuration.getInstance().getScreenHeight() - Configuration.getInstance().getOceanHeight());

        // Depth levels (darker shades)
        int levels = Configuration.getInstance().getDepthLevels();
        int levelHeight = Configuration.getInstance().getOceanHeight() / levels;

        for (int i = 0; i < levels; i++) {
            float shade = 0.2f + (i * 0.15f); // darker as we go deeper
            shapeRenderer.setColor(0f, 0f, shade, 1f);
            shapeRenderer.rect(
                0,
                i * levelHeight,
                Configuration.getInstance().getScreenWidth(),
                levelHeight
            );
        }

        shapeRenderer.end();

        // ECS updates/render
        engine.update(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
    }

    /**
     * Generates an interleaved list of different fish types for the current level.
     *
     * @param entityFactory The entity factory used to create fish entities.
     * @return An interleaved {@link LinkedList<Entity>} containing different fish types.
     */
    private LinkedList<Entity> prepareFishForLevel(IGameEntityFactory entityFactory) {
        LinkedList<Entity> clownFish = entityFactory.createFish(FishTypes.CLOWN_FISH, 10);
        LinkedList<Entity> greenFish = entityFactory.createFish(FishTypes.GREEN_FISH, 3);

        LinkedList<Entity> fishForLevel = new LinkedList<>();

        int maxSize = Math.max(clownFish.size(), greenFish.size());
        for (int i = 0; i < maxSize; i++) {
            if (i < clownFish.size()) fishForLevel.add(clownFish.get(i));
            if (i < greenFish.size()) fishForLevel.add(greenFish.get(i));
        }

        return fishForLevel;
    }

}
