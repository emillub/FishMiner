package com.github.FishMiner.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.FishMiner.FishMinerGame;
import com.github.FishMiner.ui.controller.ScreenManager;

public class MenuScreen extends AbstractScreen {

    public MenuScreen() {
        super();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        FishMinerGame.playBackgroundMusic();

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        TextButton settingsButton = new TextButton("Settings", skin);
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().setSettingScreen(new SettingScreen());
            }
        });

        TextButton playButton = new TextButton("Play", skin);
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().startGamePressed();
            }
        });

        FishMinerGame game = ScreenManager.getInstance().getGame();

        if (game.isUserLoggedIn()) {
            //Show leaderboard button if logged in
            TextButton leaderboardButton = new TextButton("Leaderboard", skin);
            leaderboardButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    // Functionality coming later
                }
            });

            rootTable.add(leaderboardButton).expand().fillX().fill().padBottom(20).row();
        } else {
            // Show login button if not logged in
            TextButton loginButton = new TextButton("Login", skin);
            loginButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    ScreenManager.getInstance().setLoginScreen(new LoginScreen());
                }
            });

            rootTable.add(loginButton).expand().fillX().fill().padBottom(20).row();
        }

        rootTable.add(playButton).expand().fillX().fill().padBottom(20).row();
        rootTable.add(settingsButton).expand().fillX().fill().row();
    }


    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        stage.act(delta);
        stage.draw();
    }
}
