package com.github.FishMiner.domain.ecs.entityFactories;

public enum FishTypes {
    // we should consider adding the dimensions of the assets here for FishWidth and FishHeight
    PINK_FISH("PINK_SQUARE.png", 9, 2, new int[]{5, 6}, 8, 10),  // Depth 5, 6, Speed 8, Weight 10
    BLUE_FISH("BLUE_SQUARE.png", 9, 2, new int[]{4, 5}, 7, 9),   // Depth 4, 5, Speed 7, Weight 9
    RED_FISH("RED_SQUARE.png", 9, 2, new int[]{3, 4}, 6, 8),     // Depth 3, 4, Speed 6, Weight 8
    GREEN_FISH("green_fish.png", 9, 2, new int[]{2, 3}, 5, 6),  // Depth 2, 3, Speed 5, Weight 6
    CLOWN_FISH("clownfish_9cols_3rows.png", 9, 3, new int[]{1, 2, 3}, 4, 5), // Depth 1, 2, 3, Speed 4, Weight 5
    GRAY_ORANGE_FISH("GRAY_ORANGE_FISH.png", 9, 2, new int[]{1, 2}, 3, 4); // Depth 1, 2, Speed 3, Weight 4

    private final String texturePath;
    private final int cols;
    private final int rows;
    private final int[] allowedDepthLevels;
    private final int speed;
    private final int weight;

    FishTypes(String texturePath, int frameCols, int frameRows, int[] allowedDepthLevels, int speed, int weight) {
        this.texturePath = texturePath;
        this.cols = frameCols;
        this.rows = frameRows;
        this.allowedDepthLevels = allowedDepthLevels;
        this.speed = speed;
        this.weight = weight;
    }

    public String getTexturePath() { return texturePath; }
    public int getFrameCols() { return cols; }
    public int getFrameRows() { return rows; }
    public int[] getAllowedDepthLevels() { return allowedDepthLevels; }
    public int getSpeed() { return speed; }
    public int getWeight() { return weight; }
}
