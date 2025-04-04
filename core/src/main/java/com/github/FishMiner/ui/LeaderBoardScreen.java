package com.github.FishMiner.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
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
    private TextField usernameField;
    private TextField scoreField;
    private Label statusLabel;

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
        //rootTable.add(titleLabel).expand().center().padBottom(20);

        usernameField = new TextField("", skin);
        usernameField.setMessageText("Enter username");

        scoreField = new TextField("", skin);
        scoreField.setMessageText("Enter score");

        TextButton submitButton = new TextButton("Submit Score", skin);
        submitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                submitScore();
            }
        });

        scoreTable = new Table();

        fetchTopScores(); // Fetch scores when screen shows

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().showMenu();
            }
        });
        statusLabel = new Label("", skin);

        Table inputTable = new Table();
        inputTable.add(new Label("Username:", skin)).padRight(10);
        inputTable.add(usernameField).width(150);
        inputTable.row().padTop(10);
        inputTable.add(new Label("Score:", skin)).padRight(10);
        inputTable.add(scoreField).width(150);
        inputTable.row().padTop(10);
        inputTable.add(submitButton).colspan(2).center();
        inputTable.row().padTop(10);
        inputTable.add(statusLabel).colspan(2).center();

        rootTable.add(inputTable).expand().fillX().fill().fillY();
        rootTable.row();
        rootTable.add(scoreTable).expand().fillX().fill().fillY();
        rootTable.row();
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
    private void submitScore() {
        FishMinerGame game = ScreenManager.getInstance().getGame();
        ILeaderBoardService leaderboard = game.getLeaderboard();

        String username = usernameField.getText();
        String scoreText = scoreField.getText();

        if (username.isEmpty() || scoreText.isEmpty()) {
            statusLabel.setText("Fields cannot be empty!");
            return;
        }

        try {
            int score = Integer.parseInt(scoreText);
            leaderboard.submitScore(username, score, new LeaderboardCallback() {
                @Override
                public void onSuccess(List<Score> scores) {
                    Gdx.app.postRunnable(() -> {
                        fetchTopScores();
                    });
                }

                @Override
                public void onFailure(String message) {
                    Gdx.app.postRunnable(() -> statusLabel.setText("Failed to submit score!"));
                }
            });

            statusLabel.setText("Score submitted successfully!");
            usernameField.setText(""); // Clear input
            scoreField.setText("");
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid score! Must be a number.");
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        stage.act(delta);
        stage.draw();
    }
}
