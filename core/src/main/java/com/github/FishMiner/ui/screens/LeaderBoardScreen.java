package com.github.FishMiner.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.FishMiner.infrastructure.Configuration;
import com.github.FishMiner.data.ScoreEntry;
import com.github.FishMiner.infrastructure.GameEventBus;
import com.github.FishMiner.domain.ports.in.data.events.LeaderboardResponseEvent;
import com.github.FishMiner.ui.ports.out.domain.events.screenEvents.ChangeScreenEvent;
import com.github.FishMiner.domain.ports.in.ui.interfaces.IGameEventListener;
import com.github.FishMiner.domain.ports.in.ui.interfaces.IGameScreen;
import com.github.FishMiner.domain.ports.in.data.events.LeaderboardFetchRequestEvent;
import com.github.FishMiner.ui.factories.ButtonFactory;
import com.github.FishMiner.ui.ports.out.domain.interfaces.IGameContext;
import com.github.FishMiner.ui.ports.out.domain.enums.ScreenType;

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
        fetchTopScores();

        rootTable = new Table();
        rootTable.setFillParent(true);
        //rootTable.setDebug(true);
        stage.addActor(rootTable);

        Label titleLabel = new Label("Leaderboard", skin);
        titleLabel.setFontScale(Configuration.getInstance().getLargeFontScale());
        rootTable.add(titleLabel).expandX().top().padTop(Configuration.getInstance().getLargePadding())
                .padBottom(Configuration.getInstance().getLargePadding()).row();


        scoreTable = new Table();

        ScrollPane scrollPane = new ScrollPane(scoreTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setStyle(new ScrollPane.ScrollPaneStyle(scrollPane.getStyle()) {
            {
                background = skin.newDrawable("white", new Color(0, 0, 0, 0.2f)); // Transparent white background
            }
        });

        rootTable.add(scrollPane)
                .expand()
                .fill()
                .row();

        TextButton backButton = ButtonFactory.createTextButton("Back to Menu", ButtonFactory.ButtonSize.MEDIUM, () -> {
            GameEventBus.getInstance().post(new ChangeScreenEvent(ScreenType.MENU));
        });
        rootTable.add(backButton)
                .expandX()
                .bottom()
                .padTop(Configuration.getInstance().getLargePadding())
                .padBottom(Configuration.getInstance().getLargePadding())
                .width(backButton.getWidth())
                .height(backButton.getHeight());

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
                    scoreTable.row();
                    continue;
                }
                String placeLabel = place + ".";
                Label placeNumber = new Label(placeLabel, skin);
                float smallerPadding = Configuration.getInstance().getSmallPadding() / 2;
                placeNumber.setFontScale(Configuration.getInstance().getSmallFontScale());

                scoreTable.row();
                scoreTable.add(placeNumber).left().padRight(smallerPadding);

                Label usernameLabel = new Label(entry.username(), skin);
                usernameLabel.setFontScale(Configuration.getInstance().getSmallFontScale());
                scoreTable.add(usernameLabel).left().pad(smallerPadding);

                Label scoreLabel = new Label(String.valueOf(entry.score()), skin);
                scoreLabel.setFontScale(Configuration.getInstance().getSmallFontScale());
                scoreTable.add(scoreLabel).right().pad(smallerPadding);

                place++;
            }
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        super.drawBackground();
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

