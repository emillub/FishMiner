package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InventoryComponent implements Component {
    //TODO: create inventory component that holds:
    //      money and various sinkers, hooks, reels the player owns.
    // can also be used for the trader entity

    public float money = 0f;
    public Set<String> ownedUpgrades = new HashSet<>();

    public boolean hasUpgrade(String upgradeId){
        return ownedUpgrades.contains(upgradeId);
    }

    public void addUpgrade(String upgradeId){
        ownedUpgrades.add(upgradeId);
    }

    public void spendMoney(int amount){
        money -= amount;
    }

}
