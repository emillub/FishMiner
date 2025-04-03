package com.github.FishMiner.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.FishMiner.FishMinerGame;
import com.github.FishMiner.data.Score;
import com.github.FishMiner.data.services.ILeaderBoardService;
import com.github.FishMiner.data.services.LeaderboardCallback;
import com.github.FishMiner.ui.controller.ScreenManager;

import java.util.List;

public class LeaderBoardScreen extends AbstractScreen {

    private Table scoreTable;

    public LeaderBoardScreen() {
        super();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.setDebug(true);
        stage.addActor(rootTable);

        Label titleLabel = new Label("Leaderboard", skin);
        rootTable.add(titleLabel).expand().center().padBottom(20);
        rootTable.row();

        scoreTable = new Table();
        rootTable.add(scoreTable).expand().fill().padTop(20);

        fetchTopScores(); // Fetch scores when screen shows

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().showMenu();
            }
        });

        rootTable.row();
        rootTable.add(backButton).expandX().fillX().padTop(20);
    }

    private void fetchTopScores() {
        FishMinerGame game = ScreenManager.getInstance().getGame();
        ILeaderBoardService leaderboard = game.getLeaderboard();
        leaderboard.getTopScores(new LeaderboardCallback() {
            @Override
            public void onSuccess(List<Score> topScores) {
                Gdx.app.postRunnable(() -> updateLeaderboardUI(topScores));
            }

            @Override
            public void onFailure(String errorMessage) {
                Gdx.app.postRunnable(() -> {
                    scoreTable.clear();
                    scoreTable.add(new Label("Error loading leaderboard: " + errorMessage, skin)).center();
                });
            }
        });
    }

    private void updateLeaderboardUI(List<Score> scores) {
        scoreTable.clear();
        for (Score entry : scores) {
            scoreTable.row().height(50);
            scoreTable.add(new Label(entry.getUsername(), skin)).left().pad(5);
            scoreTable.add(new Label(String.valueOf(entry.getScore()), skin)).right().pad(5);
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        stage.act(delta);
        stage.draw();
    }
}
