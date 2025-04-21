package com.github.FishMiner.domain.factories;

public enum SinkerTypes {
    BASIC_SINKER("BASIC_SINKER.png", "Basic Sinker", 5f, 0, 0.5f),
    HEAVY_SINKER("HEAVY_SINKER.png", "Heavy Sinker", 15f, 100, 0.5f),
            HEAVIER_SINKER("HEAVIER_SINKER.png", "Heavier Sinker", 30f, 200, 0.5f);

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
