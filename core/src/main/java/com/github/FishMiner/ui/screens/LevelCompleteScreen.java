package com.github.FishMiner.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.github.FishMiner.common.Configuration;

import com.github.FishMiner.domain.GameContext;
import com.github.FishMiner.domain.eventBus.GameEventBus;
import com.github.FishMiner.domain.events.screenEvents.ChangeScreenEvent;
import com.github.FishMiner.domain.events.ScoreEvent;
import com.github.FishMiner.domain.ports.in.IGameEventListener;
import com.github.FishMiner.ui.ports.out.ScreenType;

import java.util.Locale;

public class LevelCompleteScreen extends AbstractScreen implements IGameEventListener<ScoreEvent> {
    private float score;
    private float transitionTimer = 0f;
    private boolean sentScreenRequest;

    public LevelCompleteScreen(GameContext gameContext) {
        super(gameContext);
        screenType = ScreenType.LEVEL_COMPLETE;
        sentScreenRequest = false;
    }

    @Override
    public void show() {
        super.show();
        float finalScore = gameContext.getWorld().getScore();
        String winMessage = String.format(Locale.US, "You won with %.0f points. Good job!", finalScore);
        Label winLabel = new Label(winMessage, super.skin);
        winLabel.setFontScale(2f);
        winLabel.setAlignment(Align.center);

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        super.stage.addActor(table);
        table.add(winLabel).center().padTop(20);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        drawBackground();
        super.stage.act(delta);
        super.stage.draw();

        // After delay, go to UpgradeScreen
        transitionTimer += delta;
        // seconds before switching to UpgradeScreen
        float delayBeforeNextScreen = 1.5f;
        if (transitionTimer >= delayBeforeNextScreen && !sentScreenRequest) {
            GameEventBus.getInstance().post(new ChangeScreenEvent(ScreenType.UPGRADE));
            sentScreenRequest = true;
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
    public void onEvent(ScoreEvent event) {
        score = event.getScore();
    }

    @Override
    public Class<ScoreEvent> getEventType() {
        return ScoreEvent.class;
    }
}
