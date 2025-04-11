package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.github.FishMiner.common.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InventoryComponent implements Component {
    private static final String TAG = "InventoryComponent";
    HashMap<String, Entity> reels = new HashMap<>();
    HashMap<String, Entity> sinkers = new HashMap<>();
    HashMap<String, Entity> hooks = new HashMap<>();

    public void addSinkerToInventory(String name, Entity sinker) {
        if (sinkers.containsKey(name)) {
            Logger.getInstance().debug(TAG, "Owner already owns this sinker");
            return;
        }
        sinkers.put(name, sinker);
    }

    public void addReelToInventory(String name, Entity reel) {
        if (reels.containsKey(name)) {
            Logger.getInstance().debug(TAG, "Owner already owns this reel");
            return;
        }
        reels.put(name, reel);
    }

    public void addHooksToInventory(String name, Entity hook) {
        if (hooks.containsKey(name)) {
            Logger.getInstance().debug(TAG, "Owner already owns this hook");
            return;
        }
        reels.put(name, hook);
    }
}
