package com.github.FishMiner.domain.ecs.utils;

import com.github.FishMiner.infrastructure.Configuration;

public class DomainUtils {

    private DomainUtils() {
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
    public static int getFishStartPosX(boolean movesRight, int fishWidth) {
        // A bit static dues to screen width or fish width problems
        if (movesRight) {
            return -fishWidth - 30;
        } else {
            return Configuration.getInstance().getScreenWidth() + fishWidth - 80;
        }
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
        return (movesRight ? speed : -speed);
    }

    /**
     * Returns the min and max Y position for spawning based on depth level.
     * Depth is divided into Configuration-defined levels.
     *
     * @return int[] where 0 = minY, 1 = maxY
     */
    public static int[] getDepthIntervalFor(int depthLevel) {
        Configuration config = Configuration.getInstance();
        int spawnableOceanHeight = (int) ((float) config.getScreenHeight() * config.getOceanHeightPercentage());
        int depthLevels = Configuration.getInstance().getDepthLevels();

        int segmentHeight = spawnableOceanHeight / depthLevels;
        int minY = spawnableOceanHeight - (depthLevel * segmentHeight);
        int maxY = spawnableOceanHeight - ((depthLevel - 1) * segmentHeight);

        return new int[]{minY, maxY};
    }

    /**
     * Returns a random Y-position inside the depth level band,
     * ensuring the *entire fish sprite* (including height) fits within it.
     *
     * @param depthLevel The depth level (1 = top ocean band, etc.).
     * @param fishHeight The pixel height of the fish sprite.
     * @return A Y-position such that the entire fish is within the band.
     */
    public static float getRandomDepthFor(int depthLevel, float fishHeight) {
        int oceanHeight = Configuration.getInstance().getOceanHeight();
        int depthLevels = Configuration.getInstance().getDepthLevels();
        int segmentHeight = oceanHeight / depthLevels;

        // 💡 Flip logic: Level 1 = top, Level N = bottom
        float minY = oceanHeight - (depthLevel * segmentHeight);
        float maxY = oceanHeight - ((depthLevel - 1) * segmentHeight) - fishHeight;

        // Edge case safety
        if (maxY < minY) maxY = minY;

        return (float) (Math.random() * (maxY - minY) + minY);
    }

    public static float getLengthDepthFor(int depthLevel, int entityHeight) {
        int oceanHeight = Configuration.getInstance().getOceanHeight();
        int depthLevels = Configuration.getInstance().getDepthLevels();
        int segmentHeight = oceanHeight / depthLevels;

        return oceanHeight - ((depthLevel - 1) * segmentHeight) - entityHeight;
    }

    public static String formatEnumName(String name) {
        return name.replaceAll(" ", "_").toUpperCase();
    }
}
