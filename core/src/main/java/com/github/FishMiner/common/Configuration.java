package com.github.FishMiner.common;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

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

    private float entityScale = 1f;

    // User settings
    public final static float DEFAULT_MUSIC_VOLUME = 0.5f;
    private boolean isSoundEnabled = true;
    private float musicVolume = DEFAULT_MUSIC_VOLUME;

    private Configuration() {
        updateConfiguration(null);
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

    public void updateConfiguration(Viewport viewport) {
        if (viewport != null) {
            SCREEN_WIDTH = (int) viewport.getWorldWidth();
            SCREEN_HEIGHT = (int) viewport.getWorldHeight();
        } else {
            SCREEN_WIDTH = Gdx.graphics.getWidth();
            SCREEN_HEIGHT = Gdx.graphics.getHeight();
        }

        // Calculate scaling factors based on a reference resolution
        scaleX = SCREEN_WIDTH / RESOLUTION_X;
        scaleY = SCREEN_HEIGHT / RESOLUTION_Y;

        // UI scaling
        quarterScreenWidth = SCREEN_WIDTH / 4f;
        quarterScreenHeight = SCREEN_HEIGHT / 4f;

        bigIconWidth = SCREEN_WIDTH / 8f;
        mediumIconWidth = SCREEN_WIDTH / 12f;
        smallIconWidth = SCREEN_WIDTH / 16f;

        smallPadding = SCREEN_WIDTH / 50f;
        mediumPadding = SCREEN_WIDTH / 20f;
        largePadding = SCREEN_WIDTH / 10f;

        // Font scaling
        largeFontScale = scaleX * 4f; // Adjusted for better scaling
        mediumFontScale = scaleX * 3.2f;
        smallFontScale = scaleX * 2f;

        // Title and entity scaling
        titleScale = scaleX * 1.2f;
        entityScale = scaleX * 1.8f;
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

    public float getEntityScale() {
        return entityScale;
    }

    // Tweak these values to adjust the speed of the game
    public float getBaseMovementSpeed() {
        return 5f;
    }

    public float getBaseReelSpeed() {
        return 1f;
    }

    public float getBaseHookSpeed() {
        return 2f;
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
