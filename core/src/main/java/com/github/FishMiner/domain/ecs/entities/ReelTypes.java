package com.github.FishMiner.domain.ecs.entities;

import com.github.FishMiner.infrastructure.Assets;
import com.github.FishMiner.infrastructure.Configuration;

public enum ReelTypes {
    BASIC_REEL(Assets.getReelTexturePath("BASIC_REEL"), "Basic Reel", 8, 1, 15.0f, 4, 0,
            Configuration.getInstance().getEntityScale()),
    LONG_REEL(Assets.getReelTexturePath("LONG_REEL"), "Long Reel", 8, 1, 15.0f, 6, 100,
            Configuration.getInstance().getEntityScale()),
    FAST_REEL(Assets.getReelTexturePath("FAST_REEL"), "Fast Reel", 8, 1, 20.0f, 6, 200,
            Configuration.getInstance().getEntityScale()),
    LEGENDARY_REEL(Assets.getReelTexturePath("LEGENDARY_REEL"), "Legendary Reel", 8, 1, 40.0f, 8, 400,
            Configuration.getInstance().getEntityScale());

    private final String texturePath;
    private final String name;
    private final int frameCols;
    private final int frameRows;
    private final float returnSpeed;
    private final int depthLevel;
    private final int price;
    private final float scale;

    // Note that if the price=0 the entity will not have the upgrade component
    // Thus, the basic Reel (starting reel) should have the price as 0 and all other reels should have a price
    ReelTypes(String texturePath, String name, int frameCols, int frameRows, float returnSpeed, int depthLevel, int price) {
        this(texturePath, name, frameCols, frameRows, returnSpeed, depthLevel, price,
                Configuration.getInstance().getEntityScale());
    }

    ReelTypes(String texturePath, String name, int frameCols, int frameRows, float returnSpeed, int depthLevel,
            int price, float scale) {
        this.texturePath = texturePath;
        this.name = name;
        this.frameCols = frameCols;
        this.frameRows = frameRows;
        this.returnSpeed = returnSpeed;
        this.depthLevel = depthLevel;
        this.price = price;
        this.scale = scale;
    }

    public String getTexturePath() { return texturePath; }
    public String getName() { return name; }
    public int getFrameCols() { return frameCols; }
    public int getFrameRows() { return frameRows; }
    public float getReturnSpeed() { return returnSpeed; }
    public int getLengthLevel() { return depthLevel; }
    public int getPrice() { return price; }
    public float getScale() { return scale; }
}
