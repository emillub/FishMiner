package com.github.FishMiner.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.utils.ScreenUtils;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.entityFactories.FishTypes;
import com.github.FishMiner.domain.ecs.entityFactories.IGameEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.impl.BasicGameEntityFactory;
import com.github.FishMiner.domain.ecs.entityFactories.impl.LevelFactory;
import com.github.FishMiner.domain.ecs.systems.AnimationSystem;
import com.github.FishMiner.domain.ecs.systems.CollisionSystem;
import com.github.FishMiner.domain.ecs.systems.HookSystem;
import com.github.FishMiner.domain.ecs.systems.MovementSystem;
import com.github.FishMiner.domain.ecs.systems.RenderingSystem;
import com.github.FishMiner.domain.ecs.systems.RotationSystem;
import com.github.FishMiner.domain.ecs.systems.SpawningQueueSystem;
import com.github.FishMiner.domain.events.GameEventBus;
import com.github.FishMiner.domain.events.impl.FishHitEvent;
import com.github.FishMiner.domain.events.impl.HookReelingEvent;
import com.github.FishMiner.domain.listeners.FishStateListener;
import com.github.FishMiner.domain.listeners.HookStateListener;
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

        // Initialize the ECS engine, input controller, and sprite batch
        engine = Configuration.getInstance().getEngine();
        controller = new InputController(engine);
        batch = new SpriteBatch();

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
        //engine.addSystem(new HookInputSystem());
        System.out.println("added systems");
        // Create and add entities
        IGameEntityFactory entityFactory = new BasicGameEntityFactory(); // Abstract factory pattern

        // add the hook
        //Entity hook = entityFactory.createHook();
        //add this after debugging hook: engine.addEntity(hook);


        // create fish for this level
        LinkedList<Entity> fishForLevel = prepareFishForLevel(entityFactory);
        Entity levelEntity = LevelFactory.createEntity(fishForLevel, 6f);
        engine.addEntity(levelEntity);


        System.out.println("adding listeners");
        // Set up event listeners
        GameEventBus.getInstance().register(FishHitEvent.class, new FishStateListener());
        GameEventBus.getInstance().register(HookReelingEvent.class, new HookStateListener());
        System.out.println("added listeners");

        // Set up input processing with an InputMultiplexer
        InputMultiplexer multiplexer = new InputMultiplexer(controller, stage);
        Gdx.input.setInputProcessor(multiplexer);
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
