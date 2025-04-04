package com.github.FishMiner.domain.shop;

/**
 * Represents an upgrade/tool the player can purchase between levels.
 */

public class UpgradeItem {
    public final String id;
    public final String name;
    public final String description;
    public final int cost;
    public final String color;


    public UpgradeItem(String id, String name, String description, int cost, String color) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.color = color;
    }
}
