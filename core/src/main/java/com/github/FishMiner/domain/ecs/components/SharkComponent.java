package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.github.FishMiner.domain.ecs.util.ValidateUtil;

public class SharkComponent implements Component {
    private float damage;

    public void setDamage(float damage) {
        ValidateUtil.validateNegativeFloat(damage, "damage");
        this.damage = damage;
    }

    public float getDamage() {
        return damage;
    }
}
