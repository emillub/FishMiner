package com.github.FishMiner;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Configuration {

    private static Configuration instance;
    private static Engine engine;
    private static int SCREEN_WIDTH = Gdx.graphics.getWidth();;
    private static int SCREEN_HEIGHT = Gdx.graphics.getHeight();
    public static final float OCEAN_HEIGHT_PERCENTAGE = 0.75f;
    public static final int OCEAN_DEPTH_LEVELS = 4;
    private static final float RESOLUTION_X = 900f;
    private static final float RESOLUTION_Y = 1600f;
    private static float scaleX;
    private static float scaleY;


    private final Skin uiSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));


    private Configuration() {
        engine = new Engine();
        scaleX = SCREEN_WIDTH / RESOLUTION_X;
        scaleY = SCREEN_HEIGHT / RESOLUTION_Y;
    }

    public static Configuration getInstance() {
        if (instance == null) {
            return new Configuration();
        }
        return instance;
    }

    public Engine getEngine() {
        return engine;
    }

    public Vector2 getScaledPosition(float x, float y) {
        return new Vector2(x * scaleX, y * scaleY);
    }

    public float getScalePosX() {
        return scaleX;
    }

    public float getScalePosY() {
        return scaleY;
    }

    public void updateConfiguration() {
        SCREEN_WIDTH = Gdx.graphics.getWidth();
        SCREEN_HEIGHT = Gdx.graphics.getHeight();
        scaleX = SCREEN_WIDTH / RESOLUTION_X;
        scaleY = SCREEN_HEIGHT / RESOLUTION_Y;
    }

    public int getScreenWidth() {
        return SCREEN_WIDTH;
    }

    public int getScreenHeight() {
        return SCREEN_HEIGHT;
    }

    public float getScaleY() {
        return scaleY;
    }

    public float getGravity() {
        float gravity = 9.8f;
        return gravity * scaleY;
    }

    public float getBaseSpeed() {
        float baseSpeed = 1f;
        return baseSpeed * scaleX;
    }

    public float getOceanHeightPercentage() {
        return OCEAN_HEIGHT_PERCENTAGE;
    }

    public int getOceanHeight() {
        return (int) (SCREEN_HEIGHT * OCEAN_HEIGHT_PERCENTAGE);
    }

    public int getDepthLevels() {
        return OCEAN_DEPTH_LEVELS;
    }

    public Skin getUiSkin() {
        return uiSkin;
    }

    public boolean isDebugMode() {
        return false;
    }

}
