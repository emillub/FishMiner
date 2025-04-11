package com.github.FishMiner.domain.factories;

public enum FishTypes implements IEntityType {
    PINK_FISH("PINK_SQUARE.png", 9, 2, new int[]{5, 6}, 8, 10),
    BLUE_SHARK("shark_9cols_3rows.png", 9, 3, new int[]{4, 5}, 7, 9),
    YELLOW_BLUE_FISH("YELLOW_BLUE_FISH.png", 9, 1, new int[]{3, 4}, 6, 8, 0.6f),
    GREEN_FISH("green_fish.png", 9, 2, new int[]{2, 3}, 5, 6),
    CLOWN_FISH("clownfish_9cols_3rows.png", 9, 3, new int[]{1, 2, 3}, 4, 5),
    GRAY_ORANGE_FISH("GRAY_ORANGE_FISH.png", 9, 2, new int[]{1, 2}, 3, 4);

    private final String texturePath;
    private final int cols;
    private final int rows;
    private final int[] allowedDepthLevels;
    private final int speed;
    private final int weight;
    private final float scale;

    // Overloaded constructor (with default scale = 1.0f)
    FishTypes(String texturePath, int cols, int rows, int[] allowedDepthLevels, int speed, int weight) {
        this(texturePath, cols, rows, allowedDepthLevels, speed, weight, 1.0f);
    }

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
