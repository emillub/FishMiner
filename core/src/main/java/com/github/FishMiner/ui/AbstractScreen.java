package com.github.FishMiner.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.github.FishMiner.Configuration;

/**
 * AbstractScreen provides a common basis for all screens.
 * It initializes a Stage, loads the shared Skin, and sets up an InputMultiplexer.
 */
public abstract class AbstractScreen implements Screen {

    protected Stage stage;
    protected Skin skin;
    protected OrthographicCamera cam;

    public AbstractScreen() {
        stage = new Stage(new FitViewport(
            Configuration.getInstance().getScreenWidth(),
            Configuration.getInstance().getScreenHeight()
        ));
        skin = Configuration.getInstance().getUiSkin();
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

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
