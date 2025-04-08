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
import com.github.FishMiner.data.services.FirebaseAuthCallback;
import com.github.FishMiner.domain.ecs.components.InventoryComponent;
import com.github.FishMiner.ui.controller.ScreenManager;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.github.FishMiner.data.services.ILogInAPI;

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
        rootTable.setDebug(true);
        stage.addActor(rootTable);

        ILogInAPI loginAPI = ScreenManager.getInstance().getGame().getFirebase();
        String currentUser = loginAPI.getCurrentUsername();

        //Buttons

        TextButton playButton = new TextButton("Play", skin);
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().startGamePressed();
            }
        });

        TextButton settingsButton = new TextButton("Settings", skin);
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().setSettingScreen(new SettingScreen());
            }
        });

        //TO TEST
        TextButton testButton = new TextButton("TEST LEVEL 50", skin);
        testButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                InventoryComponent inventory = new InventoryComponent();
                inventory.money = 200;
                ScreenManager.getInstance().startNextLevel(50, inventory.money, inventory);
            }
        });

        TextButton leaderboardButton = new TextButton("Leaderboard", skin);
        leaderboardButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().setLeaderBoardScreen(new LeaderBoardScreen());
            }
        });

        //If not logged in
        if (currentUser == null || currentUser.equals("Guest")) {
            TextButton loginButton = new TextButton("Login", skin);
            loginButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    ScreenManager.getInstance().setLoginScreen(new LoginScreen());
                }
            });
            rootTable.add(loginButton).expand().fillX().fill().fillY();
            rootTable.row();

            rootTable.add(playButton).expand().fillX().fill().fillY();
            rootTable.row();
            rootTable.add(leaderboardButton).expand().fillX().fill().fillY();
            rootTable.row();
            rootTable.add(settingsButton).expand().fillX().fill().fillY();
            rootTable.row();
            rootTable.add(testButton).expand().fillX().fill().fillY();
            rootTable.row();
        } else {
            // Logged in
            rootTable.add(leaderboardButton).expand().fillX().fill().fillY();
            rootTable.row();
            rootTable.add(playButton).expand().fillX().fill().fillY();
            rootTable.row();
            rootTable.add(settingsButton).expand().fillX().fill().fillY();
            rootTable.row();
            rootTable.add(testButton).expand().fillX().fill().fillY();
            rootTable.row();

            TextButton logoutButton = new TextButton("Logout", skin);
            logoutButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    ScreenManager.getInstance().getGame().getFirebase().logout();
                    Gdx.app.postRunnable(() -> {
                        ScreenManager.getInstance().showMenu();
                    });
                }
            });
            rootTable.add(logoutButton).expand().fillX().fill().fillY();
            rootTable.row();
        }

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
