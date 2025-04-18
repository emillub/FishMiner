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
import com.github.FishMiner.common.Assets;
import com.github.FishMiner.common.Configuration;
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
        Configuration config = Configuration.getInstance();

        stage = new Stage(new FitViewport(config.getScreenWidth(), config.getScreenHeight()));
        skin = Assets.getUiskin();
        Gdx.input.setInputProcessor(stage);

        this.batch = gameContext.getBatch();
        this.shapeRenderer = gameContext.getRenderer();
        this.cam = gameContext.getCam();
        this.font = new BitmapFont();
        font.setColor(Color.BLACK);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        Configuration.getInstance().updateConfiguration();
    }

    @Override
    public void pause() {
        // Default implementation
    }

    @Override
    public void resume() {
        // Default implementation
    }

    @Override
    public void hide() {
        // Default implementation
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

    protected void drawBackground() {
        int levels = Configuration.getInstance().getDepthLevels();
        int levelHeight = Configuration.getInstance().getOceanHeight() / levels;
        int screenWidth = (int) stage.getViewport().getWorldWidth();
        int screenHeight = (int) stage.getViewport().getWorldHeight();
        int oceanHeight = Configuration.getInstance().getOceanHeight();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < levels; i++) {
            float blend = i / (float) levels;
            shapeRenderer.setColor(0f, 0f, 0.2f + blend * 0.5f, 1f);
            shapeRenderer.rect(0, i * levelHeight, screenWidth, levelHeight);
        }

        shapeRenderer.setColor(0.5f, 0.8f, 1f, 1f);
        shapeRenderer.rect(0, oceanHeight, screenWidth, screenHeight - oceanHeight);

        shapeRenderer.end();
    }
}
