package com.github.FishMiner.domain.shop;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds all available upgrade items in the game.
 */
public class UpgradeStore {
    public static Map<String, UpgradeItem> getAvailableUpgrades() {
        Map<String, UpgradeItem> store = new HashMap<>();
        store.put("long_reel", new UpgradeItem(
            "Long_reel",
            "Long Reel",
            "Reach deeper fish",
            10,
            "red"
        ));
        return store;
    }
}
