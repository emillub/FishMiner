package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;

import java.util.HashSet;
import java.util.Set;

public class InventoryComponent implements Component {

    public Set<String> ownedUpgrades = new HashSet<>();

    public boolean hasUpgrade(String upgradeId){
        return ownedUpgrades.contains(upgradeId);
    }

    public void addUpgrade(String upgradeId){
        ownedUpgrades.add(upgradeId);
    }

}
