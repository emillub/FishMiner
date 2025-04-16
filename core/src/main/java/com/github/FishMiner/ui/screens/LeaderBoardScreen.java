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
import com.github.FishMiner.data.ScoreEntry;
import com.github.FishMiner.data.ports.out.ILeaderBoardService;
import com.github.FishMiner.data.ports.out.IAuthService;
import com.github.FishMiner.domain.GameContext;
import com.github.FishMiner.domain.GameEventBus;
import com.github.FishMiner.domain.events.data.LeaderboardResponseEvent;
import com.github.FishMiner.domain.events.screenEvents.ChangeScreenEvent;
import com.github.FishMiner.domain.ports.in.IGameEventListener;
import com.github.FishMiner.domain.ports.in.data.LeaderboardCallback;
import com.github.FishMiner.domain.managers.ScreenManager;
import com.github.FishMiner.domain.ports.in.IGameScreen;
import com.github.FishMiner.ui.events.data.LeaderboardFetchRequestEvent;
import com.github.FishMiner.ui.events.data.LeaderboardPostRequestEvent;
import com.github.FishMiner.ui.ports.out.IGameContext;
import com.github.FishMiner.ui.ports.out.ScreenType;

import java.util.List;

public class LeaderBoardScreen extends AbstractScreen implements IGameScreen {
    private Table rootTable;
    private Table scoreTable;
    //private TextField scoreField;
    //private Label statusLabel;

    private final LeaderboardResponseListener responseListener = new LeaderboardResponseListener();


    public LeaderBoardScreen(IGameContext gameContext) {
        super(gameContext);
        screenType = ScreenType.LEADERBOARD;
    }

    @Override
    public void show() {
        super.show();

        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.setDebug(true);
        stage.addActor(rootTable);

        fetchTopScores();

        //scoreField = new TextField("", skin);
        //scoreField.setMessageText("Enter score");

        scoreTable = new Table();
        Table scoreContainer = new Table();
        scoreContainer.add(scoreTable).expand().fill();


        /*

        TextButton submitButton = new TextButton("Submit Score", skin);
        submitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                submitScore();
            }
        });

         */

        //fetchTopScores();

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameEventBus.getInstance().post(new ChangeScreenEvent(ScreenType.MENU));
            }
        });

        rootTable.row();
        rootTable.add(scoreContainer).expand().fill();
        rootTable.row();
        rootTable.add(backButton).padTop(20);
        //statusLabel = new Label("", skin);
        /*

        Table inputTable = new Table();
        inputTable.row().padTop(10);
        inputTable.add(new Label("Score:", skin)).padRight(10);
        inputTable.add(scoreField).width(150);
        inputTable.row().padTop(10);
        inputTable.add(submitButton).colspan(2).center();
        inputTable.row().padTop(10);
        inputTable.add(statusLabel).colspan(2).center();

        scoreTable = new Table();
        Table scoreContainer = new Table();
        scoreContainer.add(scoreTable).expand().fillX().fill().fillY();
        rootTable.row();
        rootTable.add(scoreContainer).expand().fillX().fill().fillY();
        rootTable.row();
        rootTable.add(inputTable).expand().fillX().fill().fillY();
        rootTable.row();
        rootTable.add(backButton).expand().fillX().fill().fillY();

         */

        GameEventBus.getInstance().register(responseListener);

    }

    private void fetchTopScores() {
        //FishMinerGame game = ScreenManager.getInstance().getGame();
        GameEventBus.getInstance().post(new LeaderboardFetchRequestEvent());
    }

    private void updateLeaderboardUI(List<ScoreEntry> scoreEntries) {
        // Remove all rows from the scoreTable (including layout)
        scoreTable.clearChildren(); // ✅ This removes ALL content correctly

        if (scoreEntries == null || scoreEntries.isEmpty()) {
            scoreTable.add(new Label("No scores found", skin)).center();
        } else {
            int place = 1;
            for (ScoreEntry entry : scoreEntries) {
                String placeLabel = place + ".";
                Label placeNumber = new Label(placeLabel, skin);

                scoreTable.row().height(50);
                scoreTable.add(placeNumber).left().pad(5).width(40);
                scoreTable.add(new Label(entry.username(), skin)).left().pad(5);
                scoreTable.add(new Label(String.valueOf(entry.score()), skin)).right().pad(5);

                place++;
            }
        }
    }


/*

    private void submitScore() {
        FishMinerGame game = ScreenManager.getInstance().getGame();
        IAuthService login = game.getAuthService();

        String username = login.getCurrentUsername();
        String scoreText = scoreField.getText();

        if (username == null || scoreText.isEmpty()) {
            statusLabel.setText("You're not logged in or score is empty.");
            return;
        }

        try {
            int score = Integer.parseInt(scoreText);
            ScoreEntry entry = new ScoreEntry(username, score);
            GameEventBus.getInstance().post(new LeaderboardPostRequestEvent(entry));

        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid score format.");
        }
    }

 */
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        stage.act(delta);
        stage.draw();
    }

    private class LeaderboardResponseListener implements IGameEventListener<LeaderboardResponseEvent> {

        @Override
        public void onEvent(LeaderboardResponseEvent event) {
            event.setHandled();
            Gdx.app.postRunnable(() -> {
                if (event.isSuccess()) {
                    // Only update if scores exist
                    //List<ScoreEntry> scores = event.getScores();
                    //if (scores != null && !scores.isEmpty()) {
                        updateLeaderboardUI(event.getScores());
                    }
                /*
                    if (!scoreField.getText().isEmpty()) {
                        statusLabel.setText("Score submitted!");
                        scoreField.setText("");

                        GameEventBus.getInstance().post(new LeaderboardFetchRequestEvent());
                    } else {
                        statusLabel.setText("");
                    }

                } else {
                    statusLabel.setText("Failed: " + event.getErrorMessage());
                }

                 */
            });
        }


        @Override
        public Class<LeaderboardResponseEvent> getEventType() {
            return LeaderboardResponseEvent.class;
        }
    }

    @Override
    public void hide() {
        super.hide();
        GameEventBus.getInstance().unregister(responseListener);
    }


}

