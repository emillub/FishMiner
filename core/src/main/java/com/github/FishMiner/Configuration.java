package com.github.FishMiner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Configuration {
    private static Configuration instance;
    private int screenWidth;
    private int screenHeight;

    private float scaleX;
    private float scaleY;

    private final float resolutionX = 640f;
    private final float resolutionY = 480f;

    private final Skin uiSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));


    private Configuration() {
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        scaleX = screenWidth / resolutionX;
        scaleY = screenHeight / resolutionY;
    }

    public static Configuration getInstance() {
        if (instance == null) {
            return new Configuration();
        }
        return instance;
    }

    public Vector2 getScaledPosition(float x, float y) {
        return new Vector2(x * scaleX, y * scaleY);
    }

    public void updateConfiguration() {
        this.screenWidth = Gdx.graphics.getWidth();
        this.screenHeight = Gdx.graphics.getHeight();
        this.scaleX = screenWidth / resolutionX;
        this.scaleY = screenHeight / resolutionY;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public float getScaleY() {
        return scaleY;
    }

    public float getGravity() {
        float gravity = 9.8f;
        return gravity * scaleY;
    }

    public float getBaseSpeed() {
        float baseSpeed = 100f;
        return baseSpeed * scaleX;
    }

    public int getOceanHeight() {
        return (int) (3f * screenHeight) / 4;
    }

    public int getHighestSpawnPos() {
        return this.getOceanHeight() * 4/5;
    }

    public Skin getUiSkin() {
        return uiSkin;
    }

}
