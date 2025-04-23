package com.github.FishMiner.domain.ecs.events;

import com.badlogic.ashley.core.Entity;
import com.github.FishMiner.domain.ports.in.ui.interfaces.IGameEvent;


/**
 * Inspired by Scene2D actor class
 */
public abstract class AbstractEntityEvent implements IGameEvent {
    private boolean handled;
    private Entity target;
    private Entity source;

    /**
     * Sets the event target and handled status by default
     * Handled indicates whether this event has been processed
     */
    public AbstractEntityEvent() {
        this.handled = false;
    }

    /**
     * @param target the relevant entity for this event
     */
    public AbstractEntityEvent(Entity target) {
        this();
        this.target = target;
    }

    /**
     * Constructor that sets both the target and the source
     * @param target the entity this is concerning
     * @param source the entitu sender if one exists
     */
    public AbstractEntityEvent(Entity target, Entity source) {
        this(target);
        this.source = source;
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled() {
        this.handled = true;
    }

    public Entity getTarget() {
        return target;
    }

    public Entity getSource() {
        return source;
    }
    public void setTarget(Entity target) {
        this.target = target;
    }

    public void setSource(Entity source) { this.source = source; }
}
