package com.github.FishMiner.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.FishMiner.FishMinerGame;
import com.github.FishMiner.data.Score;
import com.github.FishMiner.data.services.ILeaderBoardService;
import com.github.FishMiner.data.services.ILogInAPI;
import com.github.FishMiner.data.services.LeaderboardCallback;
import com.github.FishMiner.ui.controller.ScreenManager;

import java.util.List;

public class LeaderBoardScreen extends AbstractScreen {

    private Table scoreTable;
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
        stage.addActor(rootTable);

        // Background color
        stage.getRoot().setColor(0.1f, 0.2f, 0.3f, 1);

        // === Title ===
        Label title = new Label("LEADERBOARD", skin, "subtitle");
        title.setFontScale(2f);
        rootTable.row().padTop(30).padBottom(20).padLeft(50).padRight(50);
        rootTable.add(title).center();

        // === Score Input ===
        scoreField = new TextField("", skin);
        scoreField.setMessageText("Enter score");

        TextButton submitButton = new TextButton("Submit Score", skin);
        submitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                submitScore();
            }
        });

        statusLabel = new Label("", skin);

        Table inputTable = new Table();
        inputTable.add(new Label("Score:", skin)).padRight(10);
        inputTable.add(scoreField).width(70);
        inputTable.row().padTop(10);
        inputTable.add(submitButton).colspan(2).center();
        inputTable.row().padTop(10);
        inputTable.add(statusLabel).colspan(2).center();

        rootTable.row().padBottom(50);
        rootTable.add(inputTable).center();

        // === Leaderboard + Back Button Container ===
        Table contentWrapper = new Table();
        contentWrapper.defaults().pad(5);
        contentWrapper.padLeft(50).padRight(50);

        // Leaderboard Table
        scoreTable = new Table();
        scoreTable.defaults().pad(5);
        fetchTopScores();

        contentWrapper.row();
        contentWrapper.add(scoreTable).expandX().fillX().colspan(1);

        // === Back Button aligned with rowTable padding ===
        Table buttonWrapper = new Table();
        buttonWrapper.add(new TextButton("Back to Menu", skin, "blue-accent"))
            .padRight(10)
            .width(180)
            .height(50)
            .right()
            .expandX();

        buttonWrapper.getCells().first().getActor().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().showMenu();
            }
        });

        contentWrapper.row().padTop(30).padBottom(30);
        contentWrapper.add(buttonWrapper).fillX().expandX();

        // Add wrapper to root
        rootTable.row().expand().fill();
        rootTable.add(contentWrapper).expand().fill();
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
        int place = 1;
        for (Score entry : scores) {
            Table rowTable = new Table();
            rowTable.pad(5).padLeft(10).padRight(10);

            // Light blue row background
            rowTable.setBackground(skin.newDrawable("white", 0.3f, 0.4f, 0.9f, 0.4f)); // adjust alpha as needed

            Label rankLabel = new Label(place + ".", skin);
            Label nameLabel = new Label(entry.getUsername(), skin);
            Label scoreLabel = new Label(String.valueOf(entry.getScore()), skin);
            scoreLabel.setColor(Color.ORANGE);

            rowTable.add(rankLabel).width(40).left().pad(5);
            rowTable.add(nameLabel).expandX().left().pad(5);
            rowTable.add(scoreLabel).width(80).right().pad(5);

            scoreTable.row().padBottom(8); // vertical space between rows
            scoreTable.add(rowTable).expandX().fillX();
            place++;
        }
    }

    private TextButton createStyledButton(String text) {
        TextButton.TextButtonStyle customStyle = new TextButton.TextButtonStyle();
        customStyle.up = skin.newDrawable("white", new Color(0.3f, 0.5f, 1f, 1f)); // soft blue
        customStyle.down = skin.newDrawable("white", new Color(0.2f, 0.4f, 0.9f, 1f));
        customStyle.font = skin.getFont("default");
        customStyle.fontColor = Color.WHITE;
        return new TextButton(text, customStyle);
    }


    private void submitScore() {
        FishMinerGame game = ScreenManager.getInstance().getGame();
        ILeaderBoardService leaderboard = game.getLeaderboard();
        ILogInAPI login = game.getFirebase();

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
        ScreenUtils.clear(0.1f, 0.1f, 0.4f, 1); // dark blue background
        stage.act(delta);
        stage.draw();
    }
}
