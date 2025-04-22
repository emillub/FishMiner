package com.github.FishMiner.domain.factories;

import com.github.FishMiner.common.Assets;
import com.github.FishMiner.common.Configuration;

public enum SharkTypes implements IEntityType {
    BLUE_SHARK(Assets.getFishTexturePath("BLUE_SARK"), 9, 3, new int[] { 4, 5 }, 7, 9,
            Configuration.getInstance().getEntityScale()),;
    private final String texturePath;
    private final int cols;
    private final int rows;
    private final int[] allowedDepthLevels;
    private final int speed;
    private final int weight;
    private final float scale;

    // Overloaded constructor (with default scale = 1.0f)
    SharkTypes(String texturePath, int cols, int rows, int[] allowedDepthLevels, int speed, int weight) {
        this(texturePath, cols, rows, allowedDepthLevels, speed, weight, 1.0f);
    }

    // Main constructor with scale
    SharkTypes(String texturePath, int cols, int rows, int[] allowedDepthLevels, int speed, int weight, float scale) {
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
