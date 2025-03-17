package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.util.ValidateUtil;

public class FishComponent implements Component {
    public float scale;
    private final int value;

    public FishComponent(float scale, int value) {
        ValidateUtil.validatePositiveNumbers(scale, (float) value);
        this.scale = scale;
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
