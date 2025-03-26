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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.FishMiner.Configuration;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.github.FishMiner.ui.controller.ScreenManager;

public class LevelCompleteScreen extends AbstractScreen {

    private Stage stage;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    private Skin skin;


    private final int levelNumber;
    private final float previousScore;

    public LevelCompleteScreen(int levelNumber, float previousScore) {
        this.levelNumber = levelNumber;
        this.previousScore = previousScore;
    }


    @Override
    public void show() {
        super.show();
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(com.badlogic.gdx.graphics.Color.BLACK);

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = Configuration.getInstance().getUiSkin();

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        Label titleLabel = new Label("Fish Miner", skin);
        titleLabel.setFontScale(3f);

        Label messageLabel = new Label("You made it to the next level!", skin, "subtitle");
        messageLabel.setFontScale(1.4f);
        messageLabel.setAlignment(Align.center);

        TextButton continueButton = new TextButton("Continue", skin, "default");
        continueButton.getLabel().setFontScale(1.1f);

        Table messageBox = new Table(skin);
        messageBox.setBackground("rounded-panel");
        messageBox.defaults().pad(20).center();
        messageBox.pad(30);

        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.getInstance().startNextLevel(levelNumber + 1, previousScore);
            }
        });

        messageBox.add(messageLabel).center().row();
        messageBox.add(continueButton).width(220).height(70).center();

        table.add(titleLabel).padBottom(60).row();
        table.add(messageBox).center().row();
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

        int levels = Configuration.getInstance().getDepthLevels();
        int levelHeight = Configuration.getInstance().getOceanHeight() / levels;

        for (int i = 0; i < levels; i++) {
            float blend = i / (float) levels;
            shapeRenderer.setColor(0f, 0f, 0.2f + blend * 0.5f, 1f);
            shapeRenderer.rect(0, i * levelHeight, Gdx.graphics.getWidth(), levelHeight);
        }

        // Sky (top part)
        shapeRenderer.setColor(0.5f, 0.8f, 1f, 1f);
        shapeRenderer.rect(
            0,
            Configuration.getInstance().getOceanHeight(),
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight() - Configuration.getInstance().getOceanHeight()
        );

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
