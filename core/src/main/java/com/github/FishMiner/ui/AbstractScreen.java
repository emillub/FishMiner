package com.github.FishMiner.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.FishMiner.Configuration;

/**
 * AbstractScreen provides a common basis for all screens.
 * It initializes a Stage, loads the shared Skin, and sets up an InputMultiplexer.
 * Handles resizing logic
 */
public abstract class AbstractScreen implements Screen {

    protected Stage stage;
    protected Skin skin;

    private int lastWidth = -1;
    private int lastHeight = -1;

    public AbstractScreen() {
        stage = new Stage(new ScreenViewport()); //Responsive to device resolution
        skin = Configuration.getInstance().getUiSkin();

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        buildUI(); //Ensures UI is initialized when screen is shown
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);

        //Only rebuild UI if size has changed
        if (width != lastWidth || height != lastHeight){
            lastWidth = width;
            lastHeight = height;
            buildUI();
        }
    }

    /**
     * Rebuild all UI components.
     * Called automatically on show() and screen size change.
     */

    protected abstract void buildUI();

    @Override
    public void pause() {
        // Default implementation; override if needed
        return;
    }

    @Override
    public void resume() {
        // Default implementation; override if needed
        return;
    }

    @Override
    public void hide() {
        // Default implementation; override if needed
        return;
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
