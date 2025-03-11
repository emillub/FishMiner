package com.github.FishMiner.domain.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.IOError;
public class TextureComponent implements Component {
    private TextureRegion region;
    private int FRAME_COLS;
    private int FRAME_ROWS;

    public TextureComponent(String texturePath, int frame_rows, int frame_cols) {
        try {
            Texture texture = new Texture(texturePath);
            this.region = new TextureRegion(texture);
            this.FRAME_ROWS = frame_rows;
            this.FRAME_COLS = frame_cols;
        } catch (IOError e) {
            throw new RuntimeException(e);
        }
    }

    public TextureComponent(String texturePath) {
        this(texturePath, 1, 1);
    }

    public TextureComponent(TextureRegion region, int frame_cols) {
        this.FRAME_COLS = frame_cols;
        this.region = region;
        // Possibly set FRAME_ROWS = 1 or another default here
    }

    public int getFrameCols() {
        return FRAME_COLS;
    }

    public int getFrameRows() {
        return FRAME_ROWS;
    }

    public TextureRegion getRegion() {
        return this.region;
    }

    /**
     * Returns the width of a single animation frame,
     * based on the total region width divided by the number of columns.
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
}
