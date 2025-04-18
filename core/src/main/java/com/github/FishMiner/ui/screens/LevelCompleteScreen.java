package com.github.FishMiner.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.github.FishMiner.domain.GameEventBus;
import com.github.FishMiner.domain.events.screenEvents.ChangeScreenEvent;
import com.github.FishMiner.domain.events.ScoreEvent;
import com.github.FishMiner.domain.ports.in.IGameEventListener;
import com.github.FishMiner.domain.ports.in.IGameScreen;
import com.github.FishMiner.ui.ports.out.IGameContext;
import com.github.FishMiner.ui.ports.out.ScreenType;


import java.util.Locale;

public class LevelCompleteScreen extends AbstractScreen implements IGameScreen {
    private float score;
    private float transitionTimer = 0f;
    private boolean sentScreenRequest;

    public LevelCompleteScreen(IGameContext gameContext) {
        super(gameContext);
        screenType = ScreenType.LEVEL_COMPLETE;
        sentScreenRequest = false;
        GameEventBus.getInstance().register(getScoreEventListener());
    }

    @Override
    public void show() {
        super.show();
        if (stage.getActors().size == 0) {
            float finalScore = gameContext.getWorld().getScore();
            String winMessage = String.format(Locale.US, "You won with %.0f points. Good job!", finalScore);
            Label winLabel = new Label(winMessage, skin);
            winLabel.setFontScale(2f);
            winLabel.setAlignment(Align.center);

            Table table = new Table();
            table.setFillParent(true);
            table.center();
            stage.addActor(table);
            table.add(winLabel).center().padTop(20);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.drawBackground();
        super.stage.act(delta);
        super.stage.draw();

        // After delay, go to UpgradeScreen
        drawBackground();

        if (stage != null) {
            stage.act(delta);
            stage.draw();
        }

        transitionTimer += delta;
        // seconds before switching to UpgradeScreen
        float delayBeforeNextScreen = 1.5f;
        if (transitionTimer >= delayBeforeNextScreen && !sentScreenRequest) {
            GameEventBus.getInstance().post(new ChangeScreenEvent(ScreenType.UPGRADE));
            sentScreenRequest = true;
        }
    }

    public IGameEventListener<ScoreEvent> getScoreEventListener() {
        return new IGameEventListener<ScoreEvent>() {
            @Override
            public void onEvent(ScoreEvent event) {
                if (event.isHandled()) return;
                score = event.getScore();
            }

            @Override
            public Class<ScoreEvent> getEventType() {
                return ScoreEvent.class;
            }
        };
    }
}
