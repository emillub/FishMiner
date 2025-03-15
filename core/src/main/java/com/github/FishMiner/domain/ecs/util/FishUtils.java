package com.github.FishMiner.domain.ecs.util;

import com.github.FishMiner.Configuration;

public class FishUtils {

    private FishUtils() {
        // Prevent instantiation
    }

    /**
     * Determines the starting X position of the fish.
     * Fish that move right start at the leftmost side (0),
     * while fish that move left start at the rightmost screen boundary.
     *
     * @param movesRight True if the fish moves right, false if it moves left.
     * @return The starting X position.
     */
    public static int getFishStartPosX(boolean movesRight) {
        return movesRight ? 0 : Configuration.getInstance().getScreenWidth();
    }

    /**
     * Determines the X velocity for the fish.
     * Positive value means rightward motion, negative value means leftward motion.
     *
     * @param movesRight True if the fish moves right, false if it moves left.
     * @param speed The base speed of the fish.
     * @return The X velocity of the fish.
     */
    public static float getFishDirectionX(boolean movesRight, float speed) {
        float baseSpeed = Configuration.getInstance().getBaseSpeed();
        return (movesRight ? speed : -speed) * baseSpeed;
    }

    /**
     * Returns the min and max Y position for spawning based on depth level.
     * Depth is divided into Configuration-defined levels.
     *
     * @return int[] where 0 = minY, 1 = maxY
     */
    public static int[] getDepthIntervalFor(int depthLevel) {
        int spawnableOceanHeight = Configuration.getInstance().getOceanHeight() - 50;
        int depthLevels = Configuration.getInstance().getDepthLevels();

        int segmentHeight = spawnableOceanHeight / depthLevels;
        int minY = spawnableOceanHeight - (depthLevel * segmentHeight);
        int maxY = spawnableOceanHeight - ((depthLevel - 1) * segmentHeight);

        return new int[]{minY, maxY};
    }

    public static int getRandomDepthFor(int depthLevel) {
        int spawnableOceanHeight = Configuration.getInstance().getOceanHeight() - 50;
        int depthLevels = Configuration.getInstance().getDepthLevels();
        int segmentHeight = spawnableOceanHeight / depthLevels;
        int minY = spawnableOceanHeight - (depthLevel * segmentHeight);
        int maxY = spawnableOceanHeight - ((depthLevel - 1) * segmentHeight);
        return (int) ((Math.random() * (maxY - minY)) + minY);
    }
}
