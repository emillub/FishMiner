package com.github.FishMiner.domain.level;

import com.github.FishMiner.domain.ecs.entityFactories.FishTypes;

import java.util.List;
import java.util.Map;

public class LevelConfig {
    private final int targetScore;
    private final float spawnInterval;
    private final Map<FishTypes, Float> spawnChances;
    private final int initialFishCount;
    private final int totalFishCount;
    private final List<FishTypes> plannedFish;
    private final int numGarbage;


    public LevelConfig(
        int targetScore,
        float spawnInterval,
        Map<FishTypes, Float> spawnChances,
        int initialFishCount,
        int totalFishCount,
        List<FishTypes> plannedFish,
        int numGarbage
    ) {
        this.targetScore = targetScore;
        this.spawnInterval = spawnInterval;
        this.spawnChances = spawnChances;
        this.initialFishCount = initialFishCount;
        this.totalFishCount = totalFishCount;
        this.plannedFish = plannedFish;
        this.numGarbage = numGarbage;
    }

    public int getTargetScore() { return targetScore; }
    public float getSpawnInterval() { return spawnInterval; }
    public Map<FishTypes, Float> getSpawnChances() { return spawnChances; }
    public int getInitialFishCount() { return initialFishCount; }
    public int getTotalFishCount() { return totalFishCount; }
    public List<FishTypes> getPlannedFish() { return plannedFish; }
    public int getNumGarbage() { return numGarbage; }
}
