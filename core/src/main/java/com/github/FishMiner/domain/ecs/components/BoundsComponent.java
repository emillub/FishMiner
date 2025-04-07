package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class BoundsComponent implements Component {
    public Rectangle bounds = new Rectangle();

    public boolean overlaps(BoundsComponent bc) {
        return this.bounds.overlaps(bc.bounds);
    }

}
