package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;


public class VelocityComponent implements Component {
    public Vector2 velocity = new Vector2();

    public VelocityComponent(Vector2 velocity) {
        this.velocity = velocity;
    }
}
