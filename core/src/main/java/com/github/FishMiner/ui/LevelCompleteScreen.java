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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.FishMiner.Configuration;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.github.FishMiner.domain.ecs.components.InventoryComponent;
import com.github.FishMiner.ui.controller.ScreenManager;
import java.util.Locale;

public class LevelCompleteScreen extends AbstractScreen {

    private Stage stage;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    private Skin skin;


    private final int levelNumber;
    private final InventoryComponent inventory;


    private float transitionTimer = 0f;
    private final float delayBeforeNextScreen = 1.5f; // seconds before switching to UpgradeScreen



    public LevelCompleteScreen(int levelNumber, InventoryComponent inventory) {
        this.levelNumber = levelNumber;
        this.inventory = inventory;
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

        String winMessage = String.format(Locale.US, "You won with %.0f points. Good job!", inventory.money);
        Label winLabel = new Label(winMessage, skin);
        winLabel.setFontScale(2f);
        winLabel.setAlignment(Align.center);

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);
        table.add(winLabel).center().padTop(20);
    }



    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawBackground();

        stage.act(delta);
        stage.draw();


        // After delay, go to UpgradeScreen
        transitionTimer += delta;
        if (transitionTimer >= delayBeforeNextScreen) {
            ScreenManager.getInstance().showUpgradeScreen(levelNumber + 1, inventory);
        }
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
