package com.github.FishMiner.domain.ecs.util;

public class RandomInRangeUtil {

    private RandomInRangeUtil() {
        // prevent init
    }

    public static int getRandomIntInRange(int min, int max) {
        return (int) (((Math.random() * (max - min)) + min));
    }

    public static float getRandomFloatInRange(float min, float max) {
        return (float) ((Math.random() * (max - min)) + min);
    }

}
