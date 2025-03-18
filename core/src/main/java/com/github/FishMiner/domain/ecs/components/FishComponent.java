package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.domain.ecs.util.ValidateUtil;
import com.github.FishMiner.domain.events.GameEventBus;
import com.github.FishMiner.domain.events.IGameEvent;
import com.github.FishMiner.domain.events.impl.FishHitEvent;

public class FishComponent implements Component {
    public float scale;
    public final int value;

    public FishComponent(float scale, int value) {
        ValidateUtil.validatePositiveNumbers(scale, (float) value);
        this.scale = scale;
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
