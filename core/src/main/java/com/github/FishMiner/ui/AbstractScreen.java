package com.github.FishMiner.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.InputMultiplexer;
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
    protected InputMultiplexer multiplexer;

    public AbstractScreen() {
        stage = new Stage(new FitViewport(
            Configuration.getInstance().getScreenWidth(),
            Configuration.getInstance().getScreenHeight()
        ));
        skin = Configuration.getInstance().getUiSkin();
        multiplexer = new InputMultiplexer();
    }

    @Override
    public void show() {
        // Set the multiplexer as the global input processor
        Gdx.input.setInputProcessor(multiplexer);
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
