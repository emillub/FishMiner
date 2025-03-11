package com.github.FishMiner.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.utils.ScreenUtils;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.entityFactories.IGameEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.impl.BasicGameEntityFactory;
import com.github.FishMiner.domain.ecs.systems.AnimationSystem;
import com.github.FishMiner.domain.ecs.systems.CollisionSystem;
import com.github.FishMiner.domain.ecs.systems.HookSystem;
import com.github.FishMiner.domain.ecs.systems.MovementSystem;
import com.github.FishMiner.domain.ecs.systems.RenderingSystem;
import com.github.FishMiner.domain.ecs.systems.RotationSystem;
import com.github.FishMiner.domain.ecs.systems.SpawningSystem;
import com.github.FishMiner.domain.events.EventBus;
import com.github.FishMiner.domain.events.impl.FishHitEvent;
import com.github.FishMiner.domain.events.impl.HookReelingEvent;
import com.github.FishMiner.domain.listeners.FishStateListener;
import com.github.FishMiner.domain.listeners.HookStateListener;
import com.github.FishMiner.ui.controller.InputController;

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

//        // Create a full-width control window for gameplay UI
//        Window controlWindow = new Window("", skin, "border");
//        controlWindow.setWidth(stage.getWidth()); // Full width of the screen
//        controlWindow.defaults().pad(4f);
//        controlWindow.add("Fish Farmer Game").expandX();
//
//        // Create a Menu button that opens the overlay
//        TextButton menuButton = new TextButton("Menu", skin);
//        menuButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                // Open the PlayMenuScreen overlay
//                PlayMenuScreen playMenuOverlay = new PlayMenuScreen(stage);
//                stage.addActor(playMenuOverlay.getOverlay());
//            }
//        });
//        controlWindow.add(menuButton).pad(4f);
//        controlWindow.pack();
//        // Position the control window at the top of the screen
//        controlWindow.setPosition(0, stage.getHeight() - controlWindow.getHeight());
//        stage.addActor(controlWindow);

        // Set up event listeners
        EventBus.getInstance().register(FishHitEvent.class, new FishStateListener());
        EventBus.getInstance().register(HookReelingEvent.class, new HookStateListener());

        // Initialize the ECS engine, input controller, and sprite batch
        engine = new Engine();
        controller = new InputController(engine);
        batch = new SpriteBatch();

        // Add ECS systems
        RotationSystem rotationSystem = new RotationSystem();
        rotationSystem.setHookPosition(200, Configuration.getInstance().getOceanHeight());
        engine.addSystem(new CollisionSystem());
        engine.addSystem(rotationSystem);
        engine.addSystem(new AnimationSystem());
        engine.addSystem(new MovementSystem());
        engine.addSystem(new RenderingSystem(batch));
        engine.addSystem(new HookSystem());
        // Uncomment if needed: engine.addSystem(new HookInputSystem());

        // Create and add entities
        IGameEntityFactory entityFactory = new BasicGameEntityFactory(); // Abstract factory pattern
        // Adding more fish periodically
        SpawningSystem spawningSystem = new SpawningSystem(entityFactory);
        engine.addSystem(spawningSystem);

        Entity fish1 = entityFactory.createFish();
        Entity fish2 = entityFactory.createFish();
        Entity fish3 = entityFactory.createFish();

        Entity hook = entityFactory.createHook();
        engine.addEntity(fish1);
        engine.addEntity(fish2);
        engine.addEntity(fish3);
        engine.addEntity(hook);


        // Set up input processing with an InputMultiplexer
        InputMultiplexer multiplexer = new InputMultiplexer(controller, stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        stage.act(delta);
        engine.update(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
    }
}
