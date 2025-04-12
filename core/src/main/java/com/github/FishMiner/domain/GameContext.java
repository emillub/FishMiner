package com.github.FishMiner.domain;


import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.FishMiner.common.Configuration;
import com.github.FishMiner.domain.ecs.components.ScoreComponent;
import com.github.FishMiner.domain.ecs.systems.AnimationSystem;
import com.github.FishMiner.domain.ecs.systems.BackgroundRenderSystem;
import com.github.FishMiner.domain.ecs.systems.CollisionSystem;
import com.github.FishMiner.domain.ecs.systems.FishingSystem;
import com.github.FishMiner.domain.ecs.systems.HookInputSystem;
import com.github.FishMiner.domain.ecs.systems.HookSystem;
import com.github.FishMiner.domain.ecs.systems.MovementSystem;
import com.github.FishMiner.domain.ecs.systems.PhysicalSystem;
import com.github.FishMiner.domain.ecs.systems.RenderingSystem;
import com.github.FishMiner.domain.ecs.systems.RotationSystem;
import com.github.FishMiner.domain.ecs.systems.ScoreSystem;
import com.github.FishMiner.domain.ecs.systems.SpawningQueueSystem;
import com.github.FishMiner.domain.ecs.systems.StoreSystem;
import com.github.FishMiner.domain.ecs.systems.TransactionSystem;
import com.github.FishMiner.domain.ecs.systems.test.DebugRenderingSystem;
import com.github.FishMiner.domain.eventBus.GameEventBus;
import com.github.FishMiner.domain.events.screenEvents.ChangeScreenEvent;
import com.github.FishMiner.domain.events.screenEvents.PrepareScreenEvent;
import com.github.FishMiner.domain.level.LevelConfig;
import com.github.FishMiner.domain.level.LevelConfigFactory;
import com.github.FishMiner.domain.ports.in.IGameEvent;
import com.github.FishMiner.domain.ports.in.IGameEventListener;
import com.github.FishMiner.domain.states.WorldState;
import com.github.FishMiner.ui.ports.out.IGameContext;
import com.github.FishMiner.ui.ports.out.ScreenType;
import com.github.FishMiner.ui.ports.in.IPlayer;

public class GameContext implements IGameContext {
    private final PooledEngine engine;
    private UpgradeStore store;
    private PlayerCharacter player;
    private final World world;
    private final SpriteBatch batch;
    private final ShapeRenderer renderer;
    private final OrthographicCamera cam;

    private PrepareScreenEvent prepareScreenEvent;
    private boolean isPrepareScreenPosted = false;

    private ChangeScreenEvent sentEvent = null;

    public GameContext() {
        int worldWidth = Configuration.getInstance().getScreenWidth();
        int worldHeight = Configuration.getInstance().getScreenHeight();
        this.cam = new OrthographicCamera(worldWidth, worldHeight);
        cam.position.set(worldWidth / 2f, worldHeight / 2f, 0);

        this.batch = new SpriteBatch();
        this.renderer = new ShapeRenderer();

        // This order is very important
        this.engine = new PooledEngine();
        this.store = initUpgradeStore(engine);
        addSystemsToEngine(renderer, batch, cam, this.store);
        this.player = initPlayer(engine);
        this.world = new World(engine);

        GameEventBus.getInstance().register(world);

        initStartLevel(world);
    }

    // Add a flag to prevent repeated postings
    private boolean prepareScreenPosted = false;

    public void update(float delta) {
        if (!world.isPaused()) {
            world.update(delta);
            engine.update(delta);
        }

        // When timer is 10 seconds or less, post the PrepareScreenEvent only once.
        if (world.getTimer() <= 10 && !prepareScreenPosted) {
            prepareScreenPosted = true;
            prepareScreenEvent = new PrepareScreenEvent(ScreenType.LEVEL_COMPLETE);
            GameEventBus.getInstance().post(prepareScreenEvent);
        }

        if (world.getState() == WorldState.WON) {
            if (sentEvent == null) {
                sentEvent = new ChangeScreenEvent(ScreenType.LEVEL_COMPLETE);
                GameEventBus.getInstance().post(sentEvent);
            } else if (sentEvent.isHandled()) {
                prepareNextLevel();
                sentEvent = null;
                prepareScreenPosted = false; // reset for the next level if needed
            }
        } else if (world.getState() == WorldState.LOST) {
            if (sentEvent == null) {
                sentEvent = new ChangeScreenEvent(ScreenType.LEVEL_LOST);
                GameEventBus.getInstance().post(sentEvent);
                isPrepareScreenPosted = true;
            } else if (sentEvent.isHandled()) {
                GameEventBus.getInstance().post(new PrepareScreenEvent(ScreenType.LEADERBOARD));
                sentEvent = null;
                isPrepareScreenPosted = false;
            }
        }
    }

    public void prepareNextLevel() {
        world.nextLevel(player.getScore());
    }

    public void resetContext() {
        engine.removeEntity(player.getSinker());
        engine.removeEntity(player.getHook());
        engine.removeEntity(player.getReel());
        engine.removeEntity(player.getPlayerEntity());

        this.player = initPlayer(engine);
        initStartLevel(world);
    }

    public void createTestConfig() {
        player.getPlayerEntity().getComponent(ScoreComponent.class).setScore(99999);
        LevelConfig config = LevelConfigFactory.generateLevel(10, player.getScore());
        world.createLevel(config, 1337, 10f);
    }

    private PlayerCharacter initPlayer(PooledEngine engine) {
        Configuration config = Configuration.getInstance();
        return PlayerCharacter.getInstance(engine,
            (int) (config.getScreenWidth() * 0.5f),
            (int) (config.getScreenHeight() * config.getOceanHeightPercentage())
        );
    }

    private UpgradeStore initUpgradeStore(PooledEngine engine) {
        return UpgradeStore.getInstance(engine);
    }

    private void initStartLevel(World world) {
        LevelConfig initialConfig = LevelConfigFactory.generateLevel(1, player.getScore());
        world.createLevel(initialConfig, player.getScore());
    }

    private void addSystemsToEngine(ShapeRenderer renderer, SpriteBatch batch, OrthographicCamera cam, UpgradeStore store) {
        engine.addSystem(new BackgroundRenderSystem(renderer));
        engine.addSystem(new StoreSystem(store.getTraderEntity()));
        engine.addSystem(new RenderingSystem(batch, cam));
        engine.addSystem(new AnimationSystem());
        engine.addSystem(new MovementSystem());
        engine.addSystem(new HookSystem());
        engine.addSystem(new PhysicalSystem());
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new SpawningQueueSystem());
        engine.addSystem(new RotationSystem());

        // Register systems that are also listeners
        ScoreSystem scoreSystem = new ScoreSystem();
        engine.addSystem(scoreSystem);

        FishingSystem fishSystem = new FishingSystem();
        engine.addSystem(fishSystem);

        HookInputSystem hookInputSystem = new HookInputSystem();
        engine.addSystem(hookInputSystem);

        TransactionSystem transactionSystem = new TransactionSystem();
        engine.addSystem(transactionSystem);


        addListeners(scoreSystem, fishSystem, hookInputSystem, transactionSystem);

        if (Configuration.getInstance().isDebugMode()) {
            // toggle Debug Mode in Configuration
            engine.addSystem(new DebugRenderingSystem());
        }
    }

    /**
     * Registers all EntitySystems that are IGameEventListeners from the engine.
     * Enable this in InitializeCore() if you want to ensure that all EntitySystem listeners are added.
     * @param engine The engine used to collect all the systems
     */
    private void addListeners(PooledEngine engine) {
        ImmutableArray<EntitySystem> listenerSystems = engine.getSystems();
        GameEventBus eventBus = GameEventBus.getInstance();
        for (EntitySystem system : listenerSystems) {
            if (system instanceof IGameEventListener) {
                eventBus.register((IGameEventListener<? extends IGameEvent>) system);
            }
        }
    }

    /**
     * Registers Any class that implements IGameEventListener to the GameEventBus
     * @param args A class that implements IGameEventListener
     * @see IGameEventListener
     */
    @SafeVarargs
    private void addListeners(IGameEventListener<? extends IGameEvent>... args) {
        GameEventBus eventBus = GameEventBus.getInstance();
        for (IGameEventListener<? extends IGameEvent> listener : args) {
            eventBus.register(listener);
        }
    }

    public PooledEngine getEngine() {
        return engine;
    }

    public IPlayer getPlayer() {
        return (IPlayer) player;
    }
    public UpgradeStore getUpgradeStore() {
        return store;
    }


    public World getWorld() {
        return world;
    }

    public OrthographicCamera getCam() {
        return cam;
    }

    public ShapeRenderer getRenderer() {
        return renderer;
    }

    public SpriteBatch getBatch() {
        return batch;
    }
}
