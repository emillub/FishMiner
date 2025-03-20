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
import com.badlogic.gdx.math.Vector2;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.TransformComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.entityFactories.IEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.playerFactory.HookFactory;
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
        engine = Configuration.getInstance().getEngine();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        Vector2 hookPos = new Vector2(
            (int) (Configuration.getInstance().getScreenWidth() / 2),
            Configuration.getInstance().getOceanHeight()
        );

        IEntityFactory hookFactory = new HookFactory(engine);
        Entity hook = hookFactory.createEntity((int) hookPos.x, (int) hookPos.y);
        engine.addEntity(hook);

        // Add ECS systems
        RotationSystem rotationSystem = new RotationSystem();
        rotationSystem.setHookPosition(hookPos.x, hookPos.y);
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
        engine.update(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
    }
}
