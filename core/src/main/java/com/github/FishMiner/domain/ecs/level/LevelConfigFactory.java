package com.github.FishMiner.domain.ecs.level;

import com.github.FishMiner.domain.ecs.entityFactories.FishTypes;

import java.util.HashMap;
import java.util.Map;

public class LevelConfigFactory {

    public static LevelConfig generateLevel(int levelNumber, int previousScore) {
        // Base configuration values
        int baseTargetScore = 50;
        float baseSpawnInterval = 2.0f;

        // Adjust target score: progressively increase the target score but ensure it aligns with previous levels' progress
        int targetScore = Math.max(baseTargetScore + (levelNumber - 1) * 30, previousScore + 20);

        // Adjust spawn interval: reduce spawn interval gradually to make the game faster
        float spawnInterval = Math.max(1.5f, baseSpawnInterval - levelNumber * 0.15f); // Gradually reduce spawn interval

        // Spawn chances (weights must sum to <= 1.0f)
        Map<FishTypes, Float> spawnChances = new HashMap<>();

        // Increase difficulty by introducing new fish types and modifying chances as levels progress
        if (levelNumber >= 10) {
            spawnChances.put(FishTypes.CLOWN_FISH, 0.3f);  // Clown Fish less common in higher levels
            spawnChances.put(FishTypes.PINK_FISH, 0.4f);   // Introduce PINK_FISH earlier
            spawnChances.put(FishTypes.BLUE_FISH, 0.3f);   // More BLUE_FISH in later levels
            spawnChances.put(FishTypes.GREEN_FISH, 0.2f);  // Green Fish still important
            spawnChances.put(FishTypes.RED_FISH, 0.3f);    // RED_FISH introduced at higher levels
            spawnChances.put(FishTypes.GRAY_ORANGE_FISH, 0.1f); // Introduce this fish type at higher levels
        } else if (levelNumber >= 5) {
            spawnChances.put(FishTypes.CLOWN_FISH, 0.4f);  // Still more common in mid levels
            spawnChances.put(FishTypes.PINK_FISH, 0.3f);   // PINK_FISH introduced mid-levels
            spawnChances.put(FishTypes.GREEN_FISH, 0.2f);  // Green Fish still common
            spawnChances.put(FishTypes.BLUE_FISH, 0.1f);   // Less common early on
        } else {
            spawnChances.put(FishTypes.CLOWN_FISH, 0.6f);  // More common in early levels
            spawnChances.put(FishTypes.GREEN_FISH, 0.4f);  // Green Fish in early levels
        }

        return new LevelConfig(targetScore, spawnInterval, spawnChances);
    }
}
