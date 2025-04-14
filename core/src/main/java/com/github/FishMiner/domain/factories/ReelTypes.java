package com.github.FishMiner.domain.factories;

public enum ReelTypes {
    BASIC_REEL("BASIC_REEL.png", "BasicReel", 8, 1, 80.0f, 5, 0);
    //LONG_REEL("LONG_REEL.png", "LongReel", 1, 1, 4.5f, 6, 10),
    //FAST_REEL("FAST_REEL.png", "FastReel", 1, 1, 7.5f, 4, 15),
    //ADVANCED_REEL("ADVANCED_REEL.png", "AdvancedReel", 1, 1, 10.0f, 6, 20);

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
        this(texturePath, name, frameCols, frameRows, returnSpeed, depthLevel, price, 1.0f);
    }
    ReelTypes(String texturePath, String name, int frameCols, int frameRows, float returnSpeed, int depthLevel, int price,  float scale) {
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
