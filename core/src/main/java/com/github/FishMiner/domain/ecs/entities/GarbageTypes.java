package com.github.FishMiner.domain.ecs.entities;

import com.github.FishMiner.infrastructure.Assets;

public enum GarbageTypes implements IEntityType {
    GARBAGE(Assets.getFishTexturePath("GARBAGE"), 6, 1, new int[] { 2, 3, 4, 5, 6 }, 2);

    private final String texturePath;
    private final int cols;
    private final int rows;
    private final int[] allowedDepthLevels;
    private final int weight; // still useful to simulate hook time

    GarbageTypes(String texturePath, int frameCols, int frameRows, int[] allowedDepthLevels, int weight) {
        this.texturePath = texturePath;
        this.cols = frameCols;
        this.rows = frameRows;
        this.allowedDepthLevels = allowedDepthLevels;
        this.weight = weight;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public int getFrameCols() {
        return cols;
    }

    public int getFrameRows() {
        return rows;
    }

    public int[] getAllowedDepthLevels() {
        return allowedDepthLevels;
    }

    public int getWeight() {
        return weight;
    }
}
