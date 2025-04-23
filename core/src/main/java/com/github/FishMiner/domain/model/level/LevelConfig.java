package com.github.FishMiner.domain.model.level;

import com.github.FishMiner.domain.ecs.entities.IEntityType;
import com.github.FishMiner.domain.ecs.entities.SharkTypes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LevelConfig {
    private final int targetScore;
    private final float spawnInterval;
    private final Map<IEntityType, Float> spawnChances;
    private final int initialFishCount;
    private final int totalFishCount;
    private final List<IEntityType> plannedEntities;
    private final int numGarbage;

    public LevelConfig(
        int targetScore,
        float spawnInterval,
        Map<IEntityType, Float> spawnChances,
        int initialFishCount,
        int totalFishCount,
        List<IEntityType> plannedEntities,
        int numGarbage
    ) {
        this.targetScore = targetScore;
        this.spawnInterval = spawnInterval;
        this.spawnChances = spawnChances;
        this.initialFishCount = initialFishCount;
        this.totalFishCount = totalFishCount;
        this.plannedEntities = plannedEntities;
        this.numGarbage = numGarbage;
    }

    public int getTargetScore() { return targetScore; }
    public float getSpawnInterval() { return spawnInterval; }
    public Map<IEntityType, Float> getSpawnChances() { return spawnChances; }
    public int getInitialFishCount() { return initialFishCount; }
    public int getTotalFishCount() { return totalFishCount; }
    public List<IEntityType> getPlannedFish() {
        return plannedEntities;
    }

    public List<SharkTypes> getPlannedSharks() {
        return plannedEntities.stream()
            .filter(entity -> entity instanceof SharkTypes)
            .map(entity -> (SharkTypes) entity)
            .collect(Collectors.toList());
    }
    public int getNumGarbage() { return numGarbage; }
}
