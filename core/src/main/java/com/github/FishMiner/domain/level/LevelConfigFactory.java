package com.github.FishMiner.domain.level;

import com.github.FishMiner.domain.factories.FishTypes;
import com.github.FishMiner.domain.factories.IEntityType;
import com.github.FishMiner.domain.factories.SharkTypes;
import com.github.FishMiner.domain.factories.oceanFactory.FishFactory;

import java.util.*;

public class LevelConfigFactory {

    public static LevelConfig generateLevel(int levelNumber, int previousScore) {
        int baseTargetScore = 100;
        int initialFishCount = 6;
        int totalFishCount = Math.min(30, 10 + levelNumber * 2);
        int targetScore = Math.max(baseTargetScore + (levelNumber - 1) * 30, previousScore + 20);
        float spawnInterval = 0f;
        int numGarbage = levelNumber;

        Map<IEntityType, Float> spawnChances = new HashMap<>();

        if (levelNumber >= 10) {
            spawnChances.put(FishTypes.CLOWN_FISH, 0.3f);
            spawnChances.put(FishTypes.PINK_FISH, 0.4f);
            spawnChances.put(SharkTypes.BLUE_SHARK, 0.3f);
            spawnChances.put(FishTypes.GREEN_FISH, 0.2f);
            spawnChances.put(FishTypes.YELLOW_BLUE_FISH, 0.3f);
            spawnChances.put(FishTypes.GRAY_ORANGE_FISH, 0.1f);
            numGarbage = 3;
        } else if (levelNumber >= 5) {
            spawnChances.put(FishTypes.CLOWN_FISH, 0.4f);
            spawnChances.put(FishTypes.PINK_FISH, 0.3f);
            spawnChances.put(FishTypes.GREEN_FISH, 0.2f);
            spawnChances.put(SharkTypes.BLUE_SHARK, 0.1f);
            numGarbage = 2;
        } else {
            spawnChances.put(FishTypes.CLOWN_FISH, 0.6f);
            spawnChances.put(FishTypes.GREEN_FISH, 0.4f);
            numGarbage = 0;
        }

        List<IEntityType> plannedFish = generatePlannedFish(levelNumber, targetScore, totalFishCount, spawnChances);

        return new LevelConfig(
            targetScore,
            spawnInterval,
            spawnChances,
            initialFishCount,
            totalFishCount,
            plannedFish,
            numGarbage
        );
    }

    private static List<IEntityType> generatePlannedFish(int levelNumber, int targetScore, int totalFishCount, Map<IEntityType, Float> spawnWeights) {
        List<IEntityType> planned = new ArrayList<>();
        Random random = new Random();

        List<IEntityType> fishPool = new ArrayList<>();
        for (Map.Entry<IEntityType, Float> entry : spawnWeights.entrySet()) {
            int times = Math.round(entry.getValue() * 100);
            for (int i = 0; i < times; i++) {
                fishPool.add(entry.getKey());
            }
        }

        if (fishPool.isEmpty()) {
            fishPool.add(FishTypes.CLOWN_FISH);
        }

        int currentValue = 0;
        int buffer = 15;
        while (planned.size() < totalFishCount && currentValue < targetScore + buffer) {
            IEntityType fish = fishPool.get(random.nextInt(fishPool.size()));
            int avgDepth = average(fish.getAllowedDepthLevels());
            int value = FishFactory.calculateFishValue(avgDepth, fish.getSpeed(), fish.getWeight());

            planned.add(fish);
            currentValue += value;
        }

        while (planned.size() < totalFishCount) {
            planned.add(FishTypes.GRAY_ORANGE_FISH);
        }

        System.out.println("Level " + levelNumber + " | Planned " + planned.size() + " fish | Total Value: " + currentValue + " | Target: " + targetScore);
        return planned;
    }

    private static int average(int[] values) {
        int sum = 0;
        for (int v : values) sum += v;
        return values.length > 0 ? sum / values.length : 1;
    }
}
