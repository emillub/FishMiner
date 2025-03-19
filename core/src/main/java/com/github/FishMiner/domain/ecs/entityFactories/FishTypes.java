package com.github.FishMiner.domain.ecs.entityFactories;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public enum FishTypes {
    CLOWN_FISH("clownfish_9cols_3rows.png", 9, 3, new int[]{1}, 10, 60),
    GREEN_FISH("green_fish.png", 9, 2, new int[]{2}, 6, 20);

    // SHARK("shark_9cols_3rows.png", 9, 3, new int[]{2, 3}, 30, 100);
    // Add more as needed: e.g. BARRACUDA(new int[]{3, 4})

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
