package com.github.FishMiner.domain.level;

import com.github.FishMiner.domain.ecs.entityFactories.FishTypes;
import com.github.FishMiner.domain.ecs.entityFactories.oceanFactory.FishFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LevelConfigFactory {

    public static LevelConfig generateLevel(int levelNumber, int previousScore) {
        int baseTargetScore = 50;
        int initialFishCount = 6;
        int totalFishCount = Math.min(30, 10 + levelNumber * 2);
        int targetScore = Math.max(baseTargetScore + (levelNumber - 1) * 30, previousScore + 20);

        Map<FishTypes, Float> spawnChances = new HashMap<>();
        if (levelNumber >= 10) {
            spawnChances.put(FishTypes.CLOWN_FISH, 0.3f);
            spawnChances.put(FishTypes.PINK_FISH, 0.4f);
            spawnChances.put(FishTypes.BLUE_FISH, 0.3f);
            spawnChances.put(FishTypes.GREEN_FISH, 0.2f);
            spawnChances.put(FishTypes.RED_FISH, 0.3f);
            spawnChances.put(FishTypes.GRAY_ORANGE_FISH, 0.1f);
        } else if (levelNumber >= 5) {
            spawnChances.put(FishTypes.CLOWN_FISH, 0.4f);
            spawnChances.put(FishTypes.PINK_FISH, 0.3f);
            spawnChances.put(FishTypes.GREEN_FISH, 0.2f);
            spawnChances.put(FishTypes.BLUE_FISH, 0.1f);
        } else {
            spawnChances.put(FishTypes.CLOWN_FISH, 0.6f);
            spawnChances.put(FishTypes.GREEN_FISH, 0.4f);
        }

        List<FishTypes> plannedFish = generatePlannedFish(targetScore, levelNumber, totalFishCount, spawnChances);
        return new LevelConfig(targetScore, 0f, spawnChances, initialFishCount, totalFishCount, plannedFish);
    }

    private static List<FishTypes> generatePlannedFish(int targetScore, int levelNumber, int totalFishCount, Map<FishTypes, Float> spawnWeights) {
        List<FishTypes> planned = new ArrayList<>();
        Random random = new Random();

        // Build a weighted pool
        List<FishTypes> fishPool = new ArrayList<>();
        for (Map.Entry<FishTypes, Float> entry : spawnWeights.entrySet()) {
            int times = Math.round(entry.getValue() * 100);
            for (int i = 0; i < times; i++) {
                fishPool.add(entry.getKey());
            }
        }

        if (fishPool.isEmpty()) {
            fishPool.add(FishTypes.CLOWN_FISH);
        }

        // Accumulate fish until value surpasses targetScore
        int currentValue = 0;
        int buffer = 15; // safety buffer to avoid exact value
        while (planned.size() < totalFishCount && currentValue < targetScore + buffer) {
            FishTypes fish = fishPool.get(random.nextInt(fishPool.size()));
            int avgDepth = average(fish.getAllowedDepthLevels());
            int value = FishFactory.calculateFishValue(avgDepth, fish.getSpeed(), fish.getWeight());

            planned.add(fish);
            currentValue += value;
        }

        // If we still haven't filled the count, pad with cheapest fish
        while (planned.size() < totalFishCount) {
            planned.add(FishTypes.GRAY_ORANGE_FISH);
        }

        // Debug info
        System.out.println("Planned " + planned.size() + " fish, total value: " + currentValue + ", target: " + targetScore);

        return planned;
    }

    private static int average(int[] values) {
        int sum = 0;
        for (int v : values) sum += v;
        return values.length > 0 ? sum / values.length : 1;
    }



}
