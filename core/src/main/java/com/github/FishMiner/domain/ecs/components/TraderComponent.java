package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.List;
public class TraderComponent implements Component {
    public List<Entity> products = new ArrayList<>();
}
