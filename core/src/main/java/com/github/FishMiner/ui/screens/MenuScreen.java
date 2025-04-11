package com.github.FishMiner.ui.screens;

import static com.github.FishMiner.domain.events.soundEvents.MusicEvent.MusicCommand.PLAY_BACKGROUND;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.FishMiner.domain.GameContext;
import com.github.FishMiner.domain.eventBus.GameEventBus;
import com.github.FishMiner.domain.events.screenEvents.ChangeScreenEvent;
import com.github.FishMiner.domain.events.soundEvents.MusicEvent;
import com.github.FishMiner.domain.managers.ScreenManager;
import com.github.FishMiner.domain.ports.in.IGameScreen;
import com.github.FishMiner.ui.ports.out.ScreenType;


public class MenuScreen extends AbstractScreen implements IGameScreen {
    public MenuScreen(GameContext gameContext) {
        super(gameContext);
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(stage);
        GameEventBus.getInstance().post(new MusicEvent(PLAY_BACKGROUND));

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.setDebug(true);
        stage.addActor(rootTable);

        TextButton settingsButton = new TextButton("Settings", skin);
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().switchScreenTo(ScreenType.SETTINGS);
            }
        });
        TextButton loginButton = new TextButton("Login", skin);
        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().switchScreenTo(ScreenType.LOGIN);
            }
        });

        TextButton playButton = new TextButton("Play", skin);
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameEventBus.getInstance().post(new ChangeScreenEvent(ScreenType.MENU));
            }
        });

        //TO TEST
        TextButton testButton = new TextButton("TEST LEVEL 50", skin);
        testButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO: add a new test map or change this to tutorial
            }
        });

        TextButton leaderboardButton = new TextButton("Leaderboard", skin);
        leaderboardButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameEventBus.getInstance().post(new ChangeScreenEvent(ScreenType.LEADERBOARD));
            }
        });

        rootTable.add(loginButton).expand().fillX().fill().fillY();
        rootTable.row();
        rootTable.add(playButton).expand().fillX().fill().fillY();
        rootTable.row();
        rootTable.add(settingsButton).expand().fillX().fill().fillY();
        rootTable.row();
        rootTable.add(testButton).expand().fillX().fill().fillY();
        rootTable.row();
        rootTable.add(leaderboardButton).expand().fillX().fill().fillY();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void hide() {
        super.hide();
        stage.clear();
    }
}
