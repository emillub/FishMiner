package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;

public class WeightComponent implements Component {
    float weight;

    public WeightComponent(float weight) {
        this.weight = weight;
    }
}
