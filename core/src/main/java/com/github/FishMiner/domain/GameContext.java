package com.github.FishMiner.domain;


import static com.github.FishMiner.domain.states.WorldState.PAUSED;
import static com.github.FishMiner.ui.ports.out.ScreenType.PLAY;
import static com.github.FishMiner.ui.ports.out.ScreenType.UPGRADE;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.FishMiner.common.Configuration;
import com.github.FishMiner.common.Logger;
import com.github.FishMiner.common.ValidateUtil;
import com.github.FishMiner.domain.ecs.components.ScoreComponent;
import com.github.FishMiner.domain.ecs.systems.AnimationSystem;
import com.github.FishMiner.domain.ecs.systems.AttachmentSystem;
import com.github.FishMiner.domain.ecs.systems.BackgroundRenderSystem;
import com.github.FishMiner.domain.ecs.systems.CollisionSystem;
import com.github.FishMiner.domain.ecs.systems.FishingRodSystem;
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
import com.github.FishMiner.domain.ecs.systems.UpgradeSystem;
import com.github.FishMiner.domain.ecs.systems.test.DebugRenderingSystem;
import com.github.FishMiner.domain.events.screenEvents.ChangeScreenEvent;
import com.github.FishMiner.domain.level.LevelConfig;
import com.github.FishMiner.domain.level.LevelConfigFactory;
import com.github.FishMiner.domain.managers.ScreenManager;
import com.github.FishMiner.domain.ports.in.IGameEvent;
import com.github.FishMiner.domain.ports.in.IGameEventListener;
import com.github.FishMiner.ui.ports.out.IGameContext;
import com.github.FishMiner.ui.ports.out.ScreenType;
import com.github.FishMiner.ui.ports.in.IPlayer;

public class GameContext implements IGameContext {
    private static final String TAG = "GameContext";
    private final PooledEngine engine;
    private UpgradeStore upgradeStore;
    private PlayerCharacter player;
    private final World world;
    private final SpriteBatch batch;
    private final ShapeRenderer renderer;
    private final OrthographicCamera cam;
    private ScreenManager screenManager;
    private int currentLevel;
    public GameContext() {
        int worldWidth = Configuration.getInstance().getScreenWidth();
        int worldHeight = Configuration.getInstance().getScreenHeight();
        this.cam = new OrthographicCamera(worldWidth, worldHeight);
        cam.position.set(worldWidth / 2f, worldHeight / 2f, 0);

        this.batch = new SpriteBatch();
        this.renderer = new ShapeRenderer();

        // This order is very important
        this.engine = new PooledEngine();
        this.upgradeStore = initUpgradeStore(engine);
        addSystemsToEngine(renderer, batch, cam, this.upgradeStore);
        this.player = initPlayer(engine);
        this.world = new World(engine);

        this.currentLevel = 1;

        System.out.println("[GameContext] Systems added in order:");
        for (EntitySystem sys : engine.getSystems()) {
            System.out.println("  - " + sys.getClass().getSimpleName());
        }

        GameEventBus.getInstance().register(world);
        initStartLevel(world);
    }

    public void setScreenManager(ScreenManager screenManager) {
        ValidateUtil.validateNotNull(screenManager, TAG + "-> screenManager");
        this.screenManager = screenManager;
    }

    public void update(float delta) {
        ScreenType currentScreen = screenManager.getCurrentScreenType();
        ValidateUtil.validateNotNull(this.screenManager,  TAG + "-> screenManager");
        if (currentScreen == PLAY) {
            boolean runWorld = !world.isPaused();
            engine.getSystem(AnimationSystem.class).setProcessing(runWorld);
            engine.getSystem(AttachmentSystem.class).setProcessing(runWorld);
            engine.getSystem(CollisionSystem.class).setProcessing(runWorld);
            engine.getSystem(FishingRodSystem.class).setProcessing(runWorld);
            engine.getSystem(FishingSystem.class).setProcessing(runWorld);
            engine.getSystem(HookInputSystem.class).setProcessing(runWorld);
            engine.getSystem(MovementSystem.class).setProcessing(runWorld);
            engine.getSystem(PhysicalSystem.class).setProcessing(runWorld);
            engine.getSystem(RotationSystem.class).setProcessing(runWorld);
            engine.getSystem(SpawningQueueSystem.class).setProcessing(runWorld);
            engine.getSystem(UpgradeSystem.class).setProcessing(runWorld);
            world.update(delta);
        } else if (currentScreen == UPGRADE) {
            boolean renderStore = screenManager.getCurrentScreenType() == UPGRADE;
            engine.getSystem(StoreSystem.class).setProcessing(renderStore);
            engine.getSystem(TransactionSystem.class).setProcessing(renderStore);
            upgradeStore.update(delta);
        }
        engine.update(delta);


    }

    public void createNextLevel() {
        world.nextLevel(player.getScore());
    }

    public void createTestConfig() {
        player.getPlayerEntity().getComponent(ScoreComponent.class).setScore(9999);
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
        engine.addSystem(new AttachmentSystem());
        engine.addSystem(new StoreSystem(store.getTraderEntity()));
        engine.addSystem(new HookSystem());
        engine.addSystem(new FishingRodSystem());
        engine.addSystem(new RenderingSystem(batch, cam));
        engine.addSystem(new AnimationSystem());

        HookInputSystem hookInputSystem = new HookInputSystem();
        engine.addSystem(hookInputSystem);

        engine.addSystem(new UpgradeSystem());

        ScoreSystem scoreSystem = new ScoreSystem();
        engine.addSystem(scoreSystem);

        FishingSystem fishSystem = new FishingSystem();
        engine.addSystem(fishSystem);

        engine.addSystem(new MovementSystem());
        engine.addSystem(new PhysicalSystem());
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new SpawningQueueSystem());
        engine.addSystem(new RotationSystem());

        // Register systems that are also listeners

        TransactionSystem transactionSystem = new TransactionSystem();
        engine.addSystem(transactionSystem);


        addListeners(
            scoreSystem,
            fishSystem,
            hookInputSystem);

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
        return upgradeStore;
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
