package com.github.FishMiner.domain.ecs.entityFactories;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum FishTypes {
    CLOWN_FISH("clownfish_9cols_3rows.png", 9,3,1, 10, 60),
    SHARK("shark_9cols_3rows.png", 9,3,2, 30, 100);
    // TODO: BARRACUDA_FISH("barracuda_fish_9col_3row.png", 2, 200, 80),
    // TODO: HALIBUT_FISH("HALIBUT_FISH_9col_3row.png", 3, 150, 20);

    private final String texturePath;

    private int cols;

    private int rows;
    private final int depthLevel;
    private final int speed;
    private final int weight;

    FishTypes(String texturePath, int frameCols, int frameRows,  int depthLevel, int speed, int weight) {
        this.texturePath = texturePath;
        this.cols = frameCols;
        this.rows = frameRows;
        this.depthLevel = depthLevel;
        this.speed = speed;
        this.weight = weight;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public int getFrameCols() { return  cols; }
    public int getFrameRows() { return  rows; }
    public int getDepthLevel() {
        return depthLevel;
    }

    public int getSpeed() {
        return speed;
    }

    public int getWeight() {
        return weight;
    }

}
