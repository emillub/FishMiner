package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.github.FishMiner.common.Logger;
import java.util.HashMap;

public class InventoryComponent implements Component {
    private static final String TAG = "InventoryComponent";
    HashMap<String, Entity> reels = new HashMap<>();
    HashMap<String, Entity> sinkers = new HashMap<>();
    HashMap<String, Entity> hooks = new HashMap<>();
    private String equippedHookName = "BASIC_HOOK";
    private String equippedReelName = "BASIC_REEL";
    private String equippedSinkerName = "HEAVY_SINKER";


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

    public void equipReel(String reelName) {
        if (reels.containsKey(reelName)) {
            equippedReelName = reelName;
        }
    }

    public void equipSinker(String sinkerName) {
        if (sinkers.containsKey(sinkerName)) {
            equippedSinkerName = sinkerName;
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

    public String getEquippedReelName() {
        return equippedReelName;
    }

    public Entity getEquippedReel() {
        return reels.get(equippedReelName);
    }

    public boolean ownsReel(String name) {
        return reels.containsKey(name);
    }

    public String getEquippedSinkerName() {
        return equippedSinkerName;
    }

    public Entity getEquippedSinker() {
        return sinkers.get(equippedSinkerName);
    }

    public boolean ownsSinker(String name) {
        return sinkers.containsKey(name);
    }

}
