package com.github.FishMiner.domain.factories;

import com.github.FishMiner.common.Assets;
import com.github.FishMiner.common.Configuration;

public enum FishTypes implements IEntityType {
        PINK_FISH(Assets.getFishTexturePath("PINK_FISH"), 9, 2, new int[] { 5, 6 }, 8, 10,
                        Configuration.getInstance().getEntityScale()),
        YELLOW_BLUE_FISH(Assets.getFishTexturePath("YELLOW_BLUE_FISH"), 9, 2, new int[] { 3, 4 }, 6, 8,
                        Configuration.getInstance().getEntityScale()),
        GREEN_FISH(Assets.getFishTexturePath("GREEN_FISH"), 9, 2, new int[] { 2, 3 }, 5, 6,
                        Configuration.getInstance().getEntityScale()),
        CLOWN_FISH(Assets.getFishTexturePath("CLOWN_FISH"), 9, 3, new int[] { 1, 2, 3 }, 4, 5,
                        Configuration.getInstance().getEntityScale()),
        GRAY_ORANGE_FISH(Assets.getFishTexturePath("GRAY_ORANGE_FISH"), 9, 2, new int[] { 1, 2 }, 3, 4,
                        Configuration.getInstance().getEntityScale());

    private final String texturePath;
    private final int cols;
    private final int rows;
    private final int[] allowedDepthLevels;
    private final int speed;
    private final int weight;
    private final float scale;

    // Main constructor with scale
    FishTypes(String texturePath, int cols, int rows, int[] allowedDepthLevels, int speed, int weight, float scale) {
        this.texturePath = texturePath;
        this.cols = cols;
        this.rows = rows;
        this.allowedDepthLevels = allowedDepthLevels;
        this.speed = speed;
        this.weight = weight;
        this.scale = scale;
    }

    public String getTexturePath() { return texturePath; }
    public int getFrameCols() { return cols; }
    public int getFrameRows() { return rows; }
    public int[] getAllowedDepthLevels() { return allowedDepthLevels; }
    public int getSpeed() { return speed; }
    public int getWeight() { return weight; }
    public float getScale() { return scale; }
}
