package com.github.FishMiner.domain.ecs.level;

import com.github.FishMiner.domain.ecs.entityFactories.FishTypes;

import java.util.HashMap;
import java.util.Map;

public class LevelConfigFactory {

    public static LevelConfig generateLevel(int levelNumber) {
        // Base configuration values
        int baseTargetScore = 50;
        float baseSpawnInterval = 6.0f;
        int baseTotalFishToSpawn = 15;

        // Procedurally scale target score and spawn interval
        int targetScore = baseTargetScore + (levelNumber - 1) * 30;
        float spawnInterval = Math.max(1.5f, baseSpawnInterval - levelNumber * 0.5f);
        int totalFishToSpawn = baseTotalFishToSpawn + levelNumber * 3;

        // Spawn chances (weights must sum to <= 1.0f)
        Map<FishTypes, Float> spawnChances = new HashMap<>();
        spawnChances.put(FishTypes.CLOWN_FISH, 0.5f); // Right now, only CLOWN_FISH
        spawnChances.put(FishTypes.GREEN_FISH, 0.5f); // Right now, only CLOWN_FISH

        // Later, add: spawnChances.put(FishTypes.SHARK, 0.3f); etc.

        return new LevelConfig(targetScore, spawnInterval, spawnChances, totalFishToSpawn);
    }
}
