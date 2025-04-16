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
import com.github.FishMiner.domain.GameContext;
import com.github.FishMiner.ui.ports.out.IGameContext;
import com.github.FishMiner.ui.ports.out.ScreenType;

/**
 * AbstractScreen provides a common basis for all screens.
 * It initializes a Stage, loads the shared Skin, and sets up an InputMultiplexer.
 */
public abstract class AbstractScreen implements Screen {
    protected ScreenType screenType;
    protected IGameContext gameContext;
    protected ShapeRenderer shapeRenderer;
    protected OrthographicCamera cam;
    protected SpriteBatch batch;
    protected BitmapFont font;
    protected Stage stage;
    protected Skin skin;

    public AbstractScreen(IGameContext gameContext) {
        this.gameContext = gameContext;
        stage = new Stage(new FitViewport(
            Configuration.getInstance().getScreenWidth(),
            Configuration.getInstance().getScreenHeight()
        ));
        skin = Configuration.getInstance().getUiSkin();
        Gdx.input.setInputProcessor(stage);

        this.batch = gameContext.getBatch();
        this.shapeRenderer = gameContext.getRenderer();
        this.cam = gameContext.getCam();
        this.font = new BitmapFont();
        font.setColor(Color.BLACK);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        cam.update();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
        Configuration.getInstance().updateConfiguration();
    }

    @Override
    public void pause() {
        // Default
    }

    @Override
    public void resume() {
        // Default
    }

    @Override
    public void hide() {
        // Default
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    protected void updateCamera() {
        cam.update();
        batch.setProjectionMatrix(cam.combined);
    }

    public ScreenType getScreenType() {
        return screenType;
    }
}
