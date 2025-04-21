package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException; // Correct exception type
import com.github.FishMiner.common.Assets;
import com.github.FishMiner.common.ValidateUtil;

public class TextureComponent implements Component {
    private TextureRegion region;
    private int FRAME_COLS = 1;
    private int FRAME_ROWS = 1;

    public String texturePath;


    /**
     * Handles textures and frame-based animations by extracting rows and columns from the texture filename.
     * Example filename format: "fish_3cols_9rows.png" (3 columns, 9 rows).
     */
    public void setRegion(String texturePath, int frame_cols, int frame_rows) {
        try {
            Assets.getInstance().getAsset(texturePath, Texture.class);
            Assets.getInstance().finishLoading();
            Texture texture = new Texture(texturePath);
            this.region = new TextureRegion(texture);
            setAndValidateFrameCols(frame_cols);
            setAndValidateFrameRows(frame_rows);
        } catch (GdxRuntimeException e) {
            throw new RuntimeException("Error loading texture: " + texturePath, e);
        }
    }

    /**
     * Simplified method for textures that have no animation frames.
     * Defaults cols and rows to 1.
     * @param texturePath the path to the texture
     */
    public void setRegion(String texturePath) {
        this.setRegion(texturePath, 1, 1);
    }

    /**
     * @return The texture region representing the full texture.
     */
    public TextureRegion getRegion() {
        try {
            return this.region;
        } catch (NullPointerException e) {
            throw new IllegalStateException(texturePath + " is null: " + e.getLocalizedMessage());
        }
    }

    public int getFrameCols() {
        return FRAME_COLS;
    }

    public int getFrameRows() {
        return FRAME_ROWS;
    }


    /**
     * Gets the width of a single frame in the animation.
     * This is calculated by dividing the total texture width by the number of columns.
     *
     * @return Width of a single frame.
     */
    public int getFrameWidth() {
        try {
            return region.getRegionWidth() / FRAME_COLS;
        } catch (NullPointerException e) {
            throw new IllegalStateException(texturePath + " is null: " + e.getLocalizedMessage());
        }
    }

    /**
     * Returns the height of a single animation frame,
     * based on the total region height divided by the number of rows.
     */
    public int getFrameHeight() {
        return region.getRegionHeight() / FRAME_ROWS;
    }


    /**
     * Validates and sets the number of columns.
     * Columns are the number of frames in each (row) animation
     * @param cols The number of columns.
     */
    private void setAndValidateFrameCols(int cols) {
        try {
            ValidateUtil.validatePositiveInt(cols, "cols");
            this.FRAME_COLS = cols;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Cannot set texture cols: cannot be less than 0: " + e);
        }
    }

    /**
     * Validates and sets the number of rows.
     * rows are the number of animations in the sheet.
     * @param rows The number of rows.
     */
    private void setAndValidateFrameRows(int rows) {
        try {
            ValidateUtil.validatePositiveInt(rows, "rows");
            this.FRAME_ROWS = rows;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Cannot set texture rows: cannot be less than 0: " + e);
        }
    }
}
