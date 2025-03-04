package com.github.FishMiner.service;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.czyzby.autumn.annotation.Component;
import com.github.czyzby.autumn.annotation.Destroy;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.FishMiner.FishMinerGame;
import com.github.FishMiner.model.configuration.Configuration;
import com.github.FishMiner.model.entity.Player;
import com.github.FishMiner.service.controls.Control;

/** Manages 2D physics engine. */
@Component
public class Box2DService {
    private static final Vector2 GRAVITY = new Vector2(0f, -9.81f); // Box2D world gravity vector.
    private static final float STEP = 1f / 30f; // Length of a single Box2D step.
    private static final float WIDTH = FishMinerGame.WIDTH / 10f; // Width of Box2D world.
    private static final float HEIGHT = FishMinerGame.HEIGHT / 10f; // Height of Box2D world.
    private static final float SIZE = 3f; // Size of players.
    @Inject private ControlsService controlsService;

    private World world;
    private float timeSinceUpdate;
    private final Viewport viewport = new StretchViewport(WIDTH, HEIGHT);
    private final Array<Player> players = GdxArrays.newArray();

    /** Call this method to (re)create Box2D world according to current settings. */
    public void create() {
        dispose();
        world = new World(GRAVITY, true);
        createWorldBounds();
        final Array<Control> controls = controlsService.getControls();
        for (int index = 0; index < Configuration.PLAYERS_AMOUNT; index++) {
            final Control control = controls.get(index);
            if (control.isActive()) {
                players.add(new Player(control, getPlayerBody(index), viewport));
            }
        }
    }

    /** Creates Box2D bounds. */
    private void createWorldBounds() {
        final BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;

        final ChainShape shape = new ChainShape();
        shape.createLoop(new float[] { -WIDTH / 2f, -HEIGHT / 2f + SIZE * 2f, -WIDTH / 2f, HEIGHT / 2f, WIDTH / 2f,
                HEIGHT / 2f, WIDTH / 2f, -HEIGHT / 2f + SIZE * 2f });

        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        world.createBody(bodyDef).createFixture(fixtureDef);
        shape.dispose();
    }

    /** @param index ID of the player. Affects body size and position.
     * @return a new player body. */
    private Body getPlayerBody(final int index) {
        final BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.fixedRotation = false;
        final PolygonShape shape = new PolygonShape();
        switch (index) {
            case 0:
                bodyDef.position.set(-WIDTH / 2f + SIZE * 2f, HEIGHT / 4f);
                // Square.
                shape.setAsBox(SIZE, SIZE);
                break;
            case 1:
                bodyDef.position.set(0f, HEIGHT / 4f);
                // Hexagon. Ish.
                shape.set(new float[] { -SIZE, 0f, -SIZE / 2f, SIZE, SIZE / 2f, SIZE, SIZE, 0f, SIZE / 2f, -SIZE,
                        -SIZE / 2f, -SIZE, -SIZE, 0f });
                break;
            default:
                bodyDef.position.set(WIDTH / 2f - SIZE * 2f, HEIGHT / 4f);
                // Triangle.
                shape.set(new float[] { -SIZE, -SIZE, 0f, SIZE, SIZE, -SIZE, -SIZE, -SIZE });
        }
        final FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.restitution = 0.3f;
        fixtureDef.density = 0.05f;
        final Body body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        shape.dispose();
        return body;
    }

    /** @param delta time passed since last update. Will be used to update Box2D world. */
    public void update(final float delta) {
        timeSinceUpdate += delta;
        while (timeSinceUpdate > STEP) {
            timeSinceUpdate -= STEP;
            world.step(STEP, 8, 3);
            for (final Player player : players) {
                player.update(STEP);
            }
        }
    }

    /** @param inputMultiplexer will listen to player input events. */
    public void initiateControls(final InputMultiplexer inputMultiplexer) {
        for (final Player player : players) {
            player.getControl().attachInputListener(inputMultiplexer);
        }
    }

    /** @param width new screen width.
     * @param height new screen height. */
    public void resize(final int width, final int height) {
        viewport.update(width, height);
    }

    /** @return direct reference to current Box2D world. Might be null. */
    public World getWorld() {
        return world;
    }

    /** @return viewport with game coordinates. */
    public Viewport getViewport() {
        return viewport;
    }

    @Destroy
    public void dispose() {
        players.clear();
        if (world != null) {
            world.dispose();
            world = null;
        }
    }
}
