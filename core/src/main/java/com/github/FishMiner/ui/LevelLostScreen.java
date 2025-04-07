package com.github.FishMiner.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.FishMiner.Configuration;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.github.FishMiner.ui.controller.ScreenManager;

public class LevelLostScreen extends AbstractScreen {

    //private Stage stage;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    private Skin skin;


    public LevelLostScreen() {

    }

    @Override
    public void show() {
        super.show();
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(com.badlogic.gdx.graphics.Color.BLACK);
        buildUI();
    }

    @Override
    protected void buildUI() {
        stage.clear();

        float scale = Configuration.getInstance().getUniformScale();
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label titleLabel = new Label("Fish Miner", skin);
        titleLabel.setFontScale(3f * scale);

        Label messageLabel = new Label("You lost, start over!", skin);
        messageLabel.setFontScale(1.5f * scale);

        TextButton continueButton = new TextButton("Back to menu", skin);
        continueButton.getLabel().setFontScale(1.5f * scale);
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.getInstance().showMenu();
            }
        });

        // Creating a sub-table to act as the message box
        Table messageBox = new Table(skin);
        messageBox.setBackground("window");
        messageBox.pad(30 * scale).defaults().pad(15 * scale);
        messageBox.add(messageLabel).row();
        messageBox.add(continueButton).width(300 * scale).height(80 * scale);

        table.center().top().padTop(Gdx.graphics.getHeight() * 0.25f);
        table.add(titleLabel).padBottom(50 * scale).row();
        table.add(messageBox).center();


    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawBackground();

        stage.act(delta);
        stage.draw();
    }

    private void drawBackground() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw ocean background
        shapeRenderer.setColor(0.5f, 0.8f, 1f, 1f);
        shapeRenderer.rect(
            0,
            Configuration.getInstance().getOceanHeight(),
            Configuration.getInstance().getScreenWidth(),
            Configuration.getInstance().getScreenHeight() - Configuration.getInstance().getOceanHeight()
        );

        int levels = Configuration.getInstance().getDepthLevels();
        int levelHeight = Configuration.getInstance().getOceanHeight() / levels;
        for (int i = 0; i < levels; i++) {
            float shade = 0.2f + (i * 0.15f);
            shapeRenderer.setColor(0f, 0f, shade, 1f);
            shapeRenderer.rect(
                0,
                i * levelHeight,
                Configuration.getInstance().getScreenWidth(),
                levelHeight
            );
        }

        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
        skin.dispose();
    }
}
