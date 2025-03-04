package com.github.FishMiner.model.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.FishMiner.service.controls.Control;
import com.github.FishMiner.service.controls.ControlListener;

/** Represents a single player. */
public class Player implements ControlListener {
    private static final float DELAY = 0.75f; // Jump delay in seconds.
    private static final float SPEED = 750f; // Movement force. Affected by delta time.
    private static final float JUMP = 1000f; // Jump force.

    private final Control control;
    private final Body body;
    private final Viewport viewport;
    private boolean jumped;
    private float timeSinceLastJump = DELAY;

    public Player(final Control control, final Body body, final Viewport viewport) {
        this.control = control;
        this.body = body;
        this.viewport = viewport;
        control.setControlListener(this);
    }

    /** @return controls object that listens to player input. */
    public Control getControl() {
        return control;
    }

    /** @return Box2D body representing the player. */
    public Body getBody() {
        return body;
    }

    /** @param delta time since last update. */
    public void update(final float delta) {
        control.update(viewport, body.getPosition().x, body.getPosition().y);
        timeSinceLastJump += delta;
        final Vector2 movement = control.getMovementDirection();
        if (jumped && timeSinceLastJump > DELAY) {
            timeSinceLastJump = 0f;
            if (movement.x == 0f && movement.y == 0f) {
                body.applyForceToCenter(0f, JUMP, true);
            } else {
                body.applyForceToCenter(movement.x * JUMP, movement.y * JUMP, true);
            }
        }
        body.setActive(true);
        body.applyForceToCenter(movement.x * SPEED * delta, movement.y * SPEED * delta, true);
        jumped = false;
    }

    @Override
    public void jump() {
        jumped = true;
    }
}
