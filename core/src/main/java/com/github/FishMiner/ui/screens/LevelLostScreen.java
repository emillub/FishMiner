package com.github.FishMiner.ui.screens;

import static com.github.FishMiner.ui.ports.out.ScreenType.MENU;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.FishMiner.common.Configuration;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.github.FishMiner.domain.GameEventBus;
import com.github.FishMiner.domain.events.screenEvents.ChangeScreenEvent;
import com.github.FishMiner.domain.managers.ScreenManager;
import com.github.FishMiner.domain.ports.in.IGameScreen;
import com.github.FishMiner.ui.ports.out.IGameContext;
import com.github.FishMiner.ui.ports.out.ScreenType;

public class LevelLostScreen extends AbstractScreen implements IGameScreen {

    public LevelLostScreen(IGameContext gameContext) {
        super(gameContext);
        super.screenType = ScreenType.LEVEL_LOST;
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
        stage.addActor(table);

        Label titleLabel = new Label("Fish Miner", skin);
        titleLabel.setFontScale(3f);

        Label messageLabel = new Label("You lost, start over!", skin);
        messageLabel.setFontScale(1.2f);

        TextButton continueButton = new TextButton("Back to menu", skin);
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameEventBus.getInstance().post(new ChangeScreenEvent(MENU));
            }
        });

        // Create a sub-table to act as the message box
        Table messageBox = new Table(skin);
        messageBox.setBackground("window"); // You can customize this style in the skin
        messageBox.pad(20).defaults().pad(10);

        messageBox.add(messageLabel).row();
        messageBox.add(continueButton).width(200).height(60);

        table.center().top().padTop(Gdx.graphics.getHeight() * 0.25f);
        table.add(titleLabel).padBottom(50).row();
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
