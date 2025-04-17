package com.github.FishMiner.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
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
        //rootTable.setDebug(true);
        stage.addActor(rootTable);

        Label titleLabel = new Label("Top Scores", skin);
        titleLabel.setFontScale(2f);

        rootTable.add(titleLabel).padBottom(30).row();

        fetchTopScores();

        scoreTable = new Table();

        ScrollPane scrollPane = new ScrollPane(scoreTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        Table scoreContainer = new Table();
        scoreContainer.add(scrollPane).expand().fill();

        TextButton backButton = new TextButton("Back to Menu", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.postRunnable(() -> {
                    ScreenManager.getInstance().prepareNewScreen(ScreenType.MENU);
                    ScreenManager.getInstance().switchScreenTo(ScreenType.MENU);
                });
            }
        });

        Table contentTable = new Table();
        contentTable.add(scrollPane).expandX().fillX().height(400).padBottom(20).row();
        contentTable.add(backButton).width(250).height(60).padBottom(10).center();

        rootTable.top().padTop(50);
        rootTable.add(titleLabel).padBottom(20).row();
        rootTable.add(contentTable).expand().center();
        GameEventBus.getInstance().register(responseListener);

    }

    private void fetchTopScores() {
        GameEventBus.getInstance().post(new LeaderboardFetchRequestEvent());
    }

    private void updateLeaderboardUI(List<ScoreEntry> scoreEntries) {
        // Remove all rows from the scoreTable (including layout)
        scoreTable.clearChildren();

        if (scoreEntries == null || scoreEntries.isEmpty()) {
            scoreTable.add(new Label("No scores found", skin)).center();
        } else {
            int place = 1;
            for (ScoreEntry entry : scoreEntries) {
                if (entry == null){
                    scoreTable.row().height(30);
                    scoreTable.add(new Label("", skin)).colspan(3).padTop(10).padBottom(10);
                    continue;
                }
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

    @Override
    public void render(float delta) {
        System.out.println("[DEBUG] LeaderboardScreen render() called");
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
                        updateLeaderboardUI(event.getScores());
                    }
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

