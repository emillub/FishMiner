package com.github.FishMiner.domain.factories;

import com.github.FishMiner.common.Assets;
import com.github.FishMiner.common.Configuration;

public enum SinkerTypes {
    BASIC_SINKER(Assets.getSinkerTexturePath("BASIC_SINKER"), "Basic Sinker", 5f, 0,
            Configuration.getInstance().getEntityScale() / 2),
    HEAVY_SINKER(Assets.getSinkerTexturePath("HEAVY_SINKER"), "Heavy Sinker", 15f, 100,
            Configuration.getInstance().getEntityScale() / 2),
    HEAVIER_SINKER(Assets.getSinkerTexturePath("HEAVIER_SINKER"), "Heavier Sinker", 30f, 200,
            Configuration.getInstance().getEntityScale() / 2);

    private final String texturePath;
    private final String name;
    private final float weight;
    private final int price;
    private final float scale;

    SinkerTypes(String texturePath, String name, float weight, int price) {
        this(texturePath, name, weight, price, 1.0f);
    }

    SinkerTypes(String texturePath, String name ,float weight, int price, float scale) {
        this.texturePath = texturePath;
        this.name = name;
        this.weight = weight;
        this.price = price;
        this.scale = scale;
    }

    public String getTexturePath() { return texturePath; }
    public String getName() { return name; }
    public float getWeight() { return weight; }
    public int getPrice() { return price; }
    public float getScale() { return scale; }
}
