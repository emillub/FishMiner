package com.github.FishMiner.domain.level;

import com.github.FishMiner.domain.ecs.entityFactories.FishTypes;

import java.util.List;
import java.util.Map;

/**
 * Configuration class representing the parameters of a single level.
 * Includes spawn logic, fish distribution, and scoring targets.
 */
public class LevelConfig {
    private final int targetScore;
    private final float spawnInterval; // Optional - can be used in UI or analytics
    private final Map<FishTypes, Float> spawnChances;
    private final int initialFishCount;
    private final int totalFishCount;
    private final List<FishTypes> plannedFish;

    public LevelConfig(
        int targetScore,
        float spawnInterval,
        Map<FishTypes, Float> spawnChances,
        int initialFishCount,
        int totalFishCount,
        List<FishTypes> plannedFish
    ) {
        this.targetScore = targetScore;
        this.spawnInterval = spawnInterval;
        this.spawnChances = spawnChances;
        this.initialFishCount = initialFishCount;
        this.totalFishCount = totalFishCount;
        this.plannedFish = plannedFish;
    }

    public int getTargetScore() {
        return targetScore;
    }

    public float getSpawnInterval() {
        return spawnInterval;
    }

    public Map<FishTypes, Float> getSpawnChances() {
        return spawnChances;
    }

    public int getInitialFishCount() {
        return initialFishCount;
    }

    public int getTotalFishCount() {
        return totalFishCount;
    }

    public List<FishTypes> getPlannedFish() {
        return plannedFish;
    }
}
