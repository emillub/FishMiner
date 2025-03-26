package com.github.FishMiner.domain.ecs;

import com.badlogic.ashley.core.Entity;


/**
 * Inspired by Scene2D actor class
 */
public class EntityEvent {
    private boolean handled;
    private Entity target;

    private Entity source;

    public EntityEvent(Entity target) {
        this.target = target;
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
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
