package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;

public class RotationComponent implements Component {
    public float angle = 0f;  // Default rotation angle (0 degrees)

    public RotationComponent(float angle) {
        this.angle = angle;
    }
}
