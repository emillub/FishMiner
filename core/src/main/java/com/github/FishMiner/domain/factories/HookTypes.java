package com.github.FishMiner.domain.factories;

import com.github.FishMiner.common.Assets;
import com.github.FishMiner.common.Configuration;

public enum HookTypes {
    BASIC_HOOK(Assets.getHookTexturePath("BASIC_HOOK"), "Basic Hook", 1, 1, 0,
            Configuration.getInstance().getEntityScale(), 1.0f),
    SHARP_HOOK(Assets.getHookTexturePath("BASIC_HOOK"), "Sharp Hook", 1, 1, 200,
            Configuration.getInstance().getEntityScale(), 1.5f);

    private final String texturePath;
    private final String name;
    private final int frameCols;
    private final int frameRows;
    private final int price;
    private final float scale;
    private final float precision;


    // Short constructor: default scale = 1.0f, precision = 1.0f
    HookTypes(String texturePath, String name, int frameCols, int frameRows, int price) {
        this(texturePath, name, frameCols, frameRows, price, 1.0f, 1.0f);
    }

    HookTypes(String texturePath, String name, int frameCols, int frameRows, int price, float scale, float precision) {
        this.texturePath = texturePath;
        this.name = name;
        this.frameCols = frameCols;
        this.frameRows = frameRows;
        this.price = price;
        this.scale = scale;
        this.precision = precision;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public String getName() {
        return name;
    }

    public int getFrameCols() {
        return frameCols;
    }

    public int getFrameRows() {
        return frameRows;
    }

    public int getPrice() {
        return price;
    }

    public float getScale() {
        return scale;
    }

    public float getPrecision() {
        return precision;
    }
}
