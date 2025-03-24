package com.github.FishMiner.domain.level;

import com.github.FishMiner.domain.ecs.entityFactories.FishTypes;

import java.util.Map;

public class LevelConfig {
    private final int targetScore;
    private final float spawnInterval;
    private final Map<FishTypes, Float> spawnChances;
    private final int initialFishCount;


    public LevelConfig(int targetScore, float spawnInterval, Map<FishTypes, Float> spawnChances, int initialFishCount) {
        this.targetScore = targetScore;
        this.spawnInterval = spawnInterval;
        this.spawnChances = spawnChances;
        this.initialFishCount = initialFishCount;
    }

    public int getTargetScore() { return targetScore; }
    public float getSpawnInterval() { return spawnInterval; }
    public Map<FishTypes, Float> getSpawnChances() { return spawnChances; }
    public int getInitialFishCount() { return initialFishCount; }
}


