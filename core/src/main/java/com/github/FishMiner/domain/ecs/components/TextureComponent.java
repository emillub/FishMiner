package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException; // Correct exception type
import com.github.FishMiner.domain.ecs.util.ValidateUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextureComponent implements Component {
    private TextureRegion region;
    private int FRAME_COLS = 1;
    private int FRAME_ROWS = 1;

    /**
     * Handles textures and frame-based animations by extracting rows and columns from the texture filename.
     * Example filename format: "fish_3cols_9rows.png" (3 columns, 9 rows).
     */
    public TextureComponent(String texturePath) {
        try {
            Texture texture = new Texture(texturePath);
            this.region = new TextureRegion(texture);
        } catch (GdxRuntimeException e) { // Corrected exception type
            throw new RuntimeException("Error loading texture: " + texturePath, e);
        }

        try {
            frameColsRowsExtractor(texturePath);
        } catch (NumberFormatException e) {
            System.err.println("Rows and Cols defaulted to 1: frameColsRowsExtractor failed for path: " + texturePath);
        }
    }

    /**
     * Extracts the number of columns and rows from the texture filename.
     * Expected format: "_Xcols_Yrows" (e.g., "fish_3cols_9rows.png").
     *
     * @param texturePath The path of the texture file.
     */
    private void frameColsRowsExtractor(String texturePath) {
        Pattern pattern = Pattern.compile("_(\\d+)cols_(\\d+)rows");
        Matcher matcher = pattern.matcher(texturePath);
        if (matcher.find()) {
            int cols = Integer.parseInt(matcher.group(1));
            int rows = Integer.parseInt(matcher.group(2));
            setAndValidateFrameCols(cols);
            setAndValidateFrameRows(rows);
        } else {
            throw new NumberFormatException("Invalid texturePath: " + texturePath + ". Format must be 'fishtype_3cols_9rows'.");
        }
    }

    /**
     * @return The texture region representing the full texture.
     */
    public TextureRegion getRegion() {
        return this.region;
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
        return region.getRegionWidth() / FRAME_COLS;
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
            ValidateUtil.validatePositiveInt(cols);
            this.FRAME_COLS = cols;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Cannot set Texture rows: " + e);
        }
    }

    /**
     * Validates and sets the number of rows.
     * rows are the number of animations in the sheet.
     * @param rows The number of rows.
     */
    private void setAndValidateFrameRows(int rows) {
        try {
            ValidateUtil.validatePositiveInt(rows);
            this.FRAME_ROWS = rows;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Cannot set Texture rows: " + e);
        }
    }
}
