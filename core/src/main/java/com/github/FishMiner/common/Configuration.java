package com.github.FishMiner.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Configuration {
    private static Configuration instance;
    private static int SCREEN_WIDTH = Gdx.graphics.getWidth();;
    private static int SCREEN_HEIGHT = Gdx.graphics.getHeight();
    private static final float OCEAN_HEIGHT_PERCENTAGE = 0.75f;
    public static final int OCEAN_DEPTH_LEVELS = 6;
    private static final float RESOLUTION_X = 900f;
    private static final float RESOLUTION_Y = 1600f;

    // UI scaling
    private float quarterScreenWidth;
    private float quarterScreenHeight;

    private float bigIconWidth;
    private float mediumIconWidth;
    private float smallIconWidth;

    private float smallPadding;
    private float mediumPadding;
    private float largePadding;

    private float scaleX;
    private float scaleY;
    private float largeFontScale;
    private float mediumFontScale;
    private float smallFontScale;

    private float titleScale;

    // User settings
    public final static float DEFAULT_MUSIC_VOLUME = 0.5f;
    private boolean isSoundEnabled = true;
    private float musicVolume = DEFAULT_MUSIC_VOLUME;

    private Configuration() {
        updateConfiguration();
    }

    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
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
        scaleX = SCREEN_WIDTH / RESOLUTION_X;
        scaleY = SCREEN_HEIGHT / RESOLUTION_Y;

        quarterScreenWidth = SCREEN_WIDTH / 4f;
        quarterScreenHeight = SCREEN_HEIGHT / 4f;

        bigIconWidth = RESOLUTION_X / 10 * scaleX;
        mediumIconWidth = RESOLUTION_X / 12 * scaleX;
        smallIconWidth = RESOLUTION_X / 15 * scaleX;

        smallPadding = RESOLUTION_X / 40 * scaleX;
        mediumPadding = RESOLUTION_X / 25 * scaleX;
        largePadding = RESOLUTION_X / 5 * scaleX;

        largeFontScale = RESOLUTION_X / 300 * scaleX;
        mediumFontScale = RESOLUTION_X / 400 * scaleX;
        smallFontScale = RESOLUTION_X / 500 * scaleX;

        titleScale = RESOLUTION_X / 800 * scaleX;
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

    public boolean isDebugMode() {
        return false;
    }

    public boolean isSoundEnabled() {
        return isSoundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        isSoundEnabled = soundEnabled;
    }

    public boolean isMusicEnabled() {
        return musicVolume > 0f;
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(float musicVolume) {
        this.musicVolume = musicVolume;
    }

    public float getBigIconWidth() {
        return bigIconWidth;
    }

    public float getMediumIconWidth() {
        return mediumIconWidth;
    }

    public float getSmallIconWidth() {
        return smallIconWidth;
    }

    public float getQuarterScreenHeight() {
        return quarterScreenHeight;
    }

    public float getQuarterScreenWidth() {
        return quarterScreenWidth;
    }

    public float getSmallPadding() {
        return smallPadding;
    }

    public float getMediumPadding() {
        return mediumPadding;
    }

    public float getLargePadding() {
        return largePadding;
    }

    public float getLargeFontScale() {
        return largeFontScale;
    }


    public float getMediumFontScale() {
        return mediumFontScale;
    }
    public float getSmallFontScale() {
        return smallFontScale;
    }

    public float getTitleScale() {
        return titleScale;
    }
}
