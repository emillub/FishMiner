package com.github.FishMiner.domain.factories;

public enum SinkerTypes {
    HEAVY_SINKER("HEAVY_SINKER.png", "HeavySinker", 10f, 200, 0.5f),
    HEAVIER_SINKER("HEAVIER_SINKER.png", "HeavierSinker", 15f, 400, 0.5f);

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
