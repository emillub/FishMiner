package com.github.FishMiner.domain.ecs.entityFactories;

public enum FishTypes {
    CLOWN_FISH("clown_fish_9row_3col.png", 1, 100, 60);
    // TODO: BARRACUDA_FISH("barracuda_fish_9rows_3col.png", 2, 200, 80),
    // TODO: HALIBUT_FISH("HALIBUT_FISH_9rows_3col.png", 3, 150, 20);

    private final String texturePath;
    private final int depthLevel;
    private final int speed;
    private final int weight;

    FishTypes(String texturePath, int depthLevel, int speed, int weight) {
        this.texturePath = texturePath;
        this.depthLevel = depthLevel;
        this.speed = speed;
        this.weight = weight;
    }

    public String getTexturePath() {
        return texturePath;
    }

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
