package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;

public class FishComponent implements Component {
    public float size;
    public int value;

    public FishComponent(float size, int value) {
        if (size <= 0) {
            this.size = size;
        }
        this.value = value;
    }
}
