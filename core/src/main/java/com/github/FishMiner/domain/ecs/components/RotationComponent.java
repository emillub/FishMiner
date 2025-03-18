package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class RotationComponent implements Component {
    public float angle = 0f;  // Default rotation angle (0 degrees)
    public final Vector2 scale = new Vector2(1.0f, 1.0f);

    public RotationComponent(float angle) {
        this.angle = angle;
    }
}
