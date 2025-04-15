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
    private String equippedHookName = "BASIC_HOOK";


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
        hooks.put(name, hook);
    }

    public void equipHook(String hookName) {
        if (hooks.containsKey(hookName)) {
            equippedHookName = hookName;
        }
    }

    public String getEquippedHookName() {
        return equippedHookName;
    }

    public Entity getEquippedHook() {
        return hooks.get(equippedHookName);
    }

    public boolean ownsHook(String name) {
        return hooks.containsKey(name);
    }

}
