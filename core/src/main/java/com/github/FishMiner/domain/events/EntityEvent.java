package com.github.FishMiner.domain.events;

import com.badlogic.ashley.core.Entity;


/**
 * Inspired by Scene2D actor class
 */
public class EntityEvent {
    private boolean handled;
    private Entity target;

    private Entity source;

    /**
     * Sets the event target and handled status by default
     * Handled indicates whether this event has been processed
     * @param target the relevant entity for this event
     */
    public EntityEvent(Entity target) {
        this.target = target;
        this.handled = false;
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
