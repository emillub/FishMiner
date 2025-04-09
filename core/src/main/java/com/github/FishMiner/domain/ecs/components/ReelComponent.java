package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;

public class ReelComponent implements Component {
    public String name = "Reel";
    public int price;
    public float lineLength = 1f;
    public float returnSpeed = 1f;
}
