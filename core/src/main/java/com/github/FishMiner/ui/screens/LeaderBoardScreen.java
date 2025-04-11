package com.github.FishMiner.ui.screens;

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
import com.github.FishMiner.data.ports.out.ILeaderBoardService;
import com.github.FishMiner.data.ports.out.IAuthService;
import com.github.FishMiner.domain.GameContext;
import com.github.FishMiner.domain.eventBus.GameEventBus;
import com.github.FishMiner.domain.events.screenEvents.ChangeScreenEvent;
import com.github.FishMiner.domain.ports.out.LeaderboardCallback;
import com.github.FishMiner.domain.managers.ScreenManager;
import com.github.FishMiner.domain.ports.in.IGameScreen;
import com.github.FishMiner.ui.ports.out.ScreenType;

import java.util.List;

public class LeaderBoardScreen extends AbstractScreen implements IGameScreen {
    private Table scoreTable;
    private TextField scoreField;
    private Label statusLabel;

    public LeaderBoardScreen(GameContext gameContext) {
        super(gameContext);
        screenType = ScreenType.LEADERBOARD;
    }

    @Override
    public void show() {
        super.show();

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.setDebug(true);
        stage.addActor(rootTable);

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

        fetchTopScores();

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameEventBus.getInstance().post(new ChangeScreenEvent(ScreenType.MENU));
            }
        });
        statusLabel = new Label("", skin);

        Table inputTable = new Table();
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
        rootTable.add(backButton).expand().fillX().fill().fillY();
    }

    private void fetchTopScores() {
        FishMinerGame game = ScreenManager.getInstance().getGame();
        ILeaderBoardService leaderboard = game.getLeaderBoardService();
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
        int place = 1;
        for (Score entry : scores) {
            String placeLabel = place + ".";
            Label placeNumber = new Label(placeLabel, skin);

            scoreTable.row().height(50);
            scoreTable.add(placeNumber).left().pad(5).width(40);
            scoreTable.add(new Label(entry.username(), skin)).left().pad(5);
            scoreTable.add(new Label(String.valueOf(entry.score()), skin)).right().pad(5);

            place++;
        }
    }
    private void submitScore() {
        FishMinerGame game = ScreenManager.getInstance().getGame();
        ILeaderBoardService leaderboard = game.getLeaderBoardService();
        IAuthService login = game.getAuthService();

        String username = login.getCurrentUsername();
        String scoreText = scoreField.getText();

        if (username == null || scoreText.isEmpty()) {
            statusLabel.setText("You're not logged in or score is empty.");
            return;
        }

        try {
            int score = Integer.parseInt(scoreText);
            leaderboard.submitScore(username, score, new LeaderboardCallback() {
                @Override
                public void onSuccess(List<Score> scores) {
                    Gdx.app.postRunnable(() -> {
                        statusLabel.setText("Score submitted!");
                        scoreField.setText("");
                        fetchTopScores();
                    });
                }

                @Override
                public void onFailure(String message) {
                    Gdx.app.postRunnable(() -> statusLabel.setText("Failed to submit score."));
                }
            });
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid score format.");
        }
    }
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        stage.act(delta);
        stage.draw();
    }

}
