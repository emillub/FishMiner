package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.github.FishMiner.infrastructure.Logger;
import com.github.FishMiner.domain.ecs.utils.DomainUtils;

public class ReelComponent implements Component {
    private static final String TAG = "ReelComponent";
    public String name = "";
    public int price;
    int[] interval = DomainUtils.getDepthIntervalFor(4);
    public float lineLength = interval[1];
    public float returnSpeed = 100f;


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        if (name.isEmpty() || name.isBlank()) {
            IllegalStateException exception = new IllegalStateException("Upgrade name cannot be empty");
            Logger.getInstance().error(TAG, "Refused to return empty upgrade name" + getName(), exception);
            throw exception;
        }
        return name;
    }
}
