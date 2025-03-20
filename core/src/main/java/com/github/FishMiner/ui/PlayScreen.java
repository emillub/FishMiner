package com.github.FishMiner.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.utils.ScreenUtils;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.components.HookComponent;
import com.github.FishMiner.domain.ecs.components.PositionComponent;
import com.github.FishMiner.domain.ecs.components.StateComponent;
import com.github.FishMiner.domain.ecs.entityFactories.FishTypes;
import com.github.FishMiner.domain.ecs.entityFactories.IGameEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.impl.BasicGameEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.impl.LevelFactory;
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
    private InputController controller;

    @Override
    public void show() {
        super.show();

        engine = Configuration.getInstance().getEngine();
        //controller = new InputController(engine);
        batch = new SpriteBatch();
        // Create and add entities
        IGameEntityFactory entityFactory = new BasicGameEntityFactory(); // Abstract factory pattern

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
        engine.addSystem(new SpawningQueueSystem());
        engine.addSystem(new PhysicalSystem());

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

        // create fish for this level
        LinkedList<Entity> fishForLevel = prepareFishForLevel(entityFactory);
        Entity levelEntity = LevelFactory.createEntity(fishForLevel, 6f);
        engine.addEntity(levelEntity);


        System.out.println("adding listeners");
        // Set up event listeners
        //GameEventBus.getInstance().register(new FishStateListener());
        //GameEventBus.getInstance().register(new HookStateListener());
        System.out.println("added listeners");

        Gdx.input.setInputProcessor(stage);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.SPACE) {
                    // Retrieve the hook entity from the engine.
                    ImmutableArray<Entity> hooks = engine.getEntitiesFor(Family.all(HookComponent.class, PositionComponent.class, StateComponent.class).get());
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
        ScreenUtils.clear(0f, 0.1f, 0.7f, 0.7f);
        stage.act(delta);
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
        LinkedList<Entity> sharks = entityFactory.createFish(FishTypes.SHARK, 3);

        LinkedList<Entity> fishForLevel = new LinkedList<>();

        int maxSize = Math.max(clownFish.size(), sharks.size());
        for (int i = 0; i < maxSize; i++) {
            if (i < clownFish.size()) fishForLevel.add(clownFish.get(i));
            if (i < sharks.size()) fishForLevel.add(sharks.get(i));
        }

        return fishForLevel;
    }

}
