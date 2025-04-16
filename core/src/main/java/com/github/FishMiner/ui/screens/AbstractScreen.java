package com.github.FishMiner.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.github.FishMiner.common.Configuration;
import com.github.FishMiner.domain.managers.ScreenManager;
import com.github.FishMiner.ui.ports.out.IGameContext;
import com.github.FishMiner.ui.ports.out.ScreenType;

public abstract class AbstractScreen implements Screen {
    protected ScreenType screenType;
    protected IGameContext gameContext;
    protected ShapeRenderer shapeRenderer;
    protected OrthographicCamera cam;
    protected SpriteBatch batch;
    protected BitmapFont font;
    protected Stage stage;
    protected Skin skin;

    private boolean initialized = false;

    public AbstractScreen(IGameContext gameContext) {
        this.gameContext = gameContext;
        this.batch = gameContext.getBatch();
        this.shapeRenderer = gameContext.getRenderer();
        this.cam = gameContext.getCam();
        this.font = new BitmapFont();
        font.setColor(Color.BLACK);
    }

    protected void initializeStageIfNeeded() {
        if (!initialized) {
            stage = new Stage(new FitViewport(
                Configuration.getInstance().getScreenWidth(),
                Configuration.getInstance().getScreenHeight()
            ));
            skin = Configuration.getInstance().getUiSkin();
            initialized = true;
        }
    }

    @Override
    public void show() {
        initializeStageIfNeeded();
        if (ScreenManager.getInstance().getCurrentScreen() == this) {
            Gdx.input.setInputProcessor(stage);
        }
    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height);
        }
        Configuration.getInstance().updateConfiguration();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (skin != null) skin.dispose();
    }

    protected void updateCamera() {
        cam.update();
        batch.setProjectionMatrix(cam.combined);
    }

    public ScreenType getScreenType() {
        return screenType;
    }
}
