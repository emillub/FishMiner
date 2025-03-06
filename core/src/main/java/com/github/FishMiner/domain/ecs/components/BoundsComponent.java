package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class BoundsComponent implements Component {
    public Rectangle bounds;

    public BoundsComponent(Vector2 position, float width, float height) {
        this.bounds = new Rectangle(position.x, position.y, width, height);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean overlaps(BoundsComponent bc) {
        return this.bounds.overlaps(bc.bounds);
    }

}
