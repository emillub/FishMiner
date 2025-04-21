package com.github.FishMiner.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.FishMiner.common.Assets;
import com.github.FishMiner.common.Configuration;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.github.FishMiner.data.ScoreEntry;
import com.github.FishMiner.domain.GameEventBus;
import com.github.FishMiner.domain.events.data.LeaderboardResponseEvent;
import com.github.FishMiner.domain.events.screenEvents.ChangeScreenEvent;
import com.github.FishMiner.domain.managers.ScreenManager;
import com.github.FishMiner.domain.ports.in.IGameEventListener;
import com.github.FishMiner.domain.ports.in.IGameScreen;
import com.github.FishMiner.domain.session.UserSession;
import com.github.FishMiner.ui.events.data.LeaderboardPostRequestEvent;
import com.github.FishMiner.ui.factories.ButtonFactory;
import com.github.FishMiner.ui.factories.ButtonFactory.ButtonSize;
import com.github.FishMiner.ui.ports.out.IGameContext;
import com.github.FishMiner.ui.ports.out.ScreenType;
import com.github.FishMiner.FishMinerGame;
import com.github.FishMiner.domain.GameContext;

public class LevelLostScreen extends AbstractScreen implements IGameScreen {

    public LevelLostScreen(IGameContext gameContext) {
        super(gameContext);
        super.screenType = ScreenType.LEVEL_LOST;
    }


    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(stage);

        skin = Assets.getUiskin();

        //Post score once when screen is shown
        FishMinerGame game = ScreenManager.getInstance().getGame();
        String username = game.getAuthService().getCurrentUsername();
        float score = gameContext.getWorld().getScore();

        if (username != null) {
            ScoreEntry entry = new ScoreEntry(username, (int) score);
            GameEventBus.getInstance().post(new LeaderboardPostRequestEvent(entry));
        }

        Label messageLabel = new Label("Game over!", skin);
        messageLabel.setFontScale(Configuration.getInstance().getMediumFontScale());

        Label scoreLabel = new Label("Your Score: " + (int) score, skin);
        scoreLabel.setFontScale(Configuration.getInstance().getSmallFontScale());

        TextButton seeLeaderboardButton = ButtonFactory.createTextButton("See Leaderboard", ButtonSize.MEDIUM, () -> {
            GameEventBus.getInstance().post(new ChangeScreenEvent(ScreenType.LEADERBOARD));
        });

        TextButton backToMenuButton = ButtonFactory.createTextButton("Back to Menu", ButtonSize.MEDIUM, () -> {
            GameEventBus.getInstance().post(new ChangeScreenEvent(ScreenType.MENU));
        });



        // Create a sub-table to act as the message box
        Image titleImage = super.getTitleImage();
        stage.addActor(titleImage);

        Table middleSection = new Table();
        middleSection.setFillParent(true);
        middleSection.center().padTop(Configuration.getInstance().getLargePadding());

        middleSection.add(messageLabel).padBottom(Configuration.getInstance().getMediumPadding()).row();
        middleSection.add(scoreLabel).padBottom(Configuration.getInstance().getMediumPadding()).row();
        middleSection.add(seeLeaderboardButton).size(seeLeaderboardButton.getWidth(), seeLeaderboardButton.getHeight())
                .padBottom(Configuration.getInstance().getSmallPadding())
                .padTop(Configuration.getInstance().getMediumPadding()).top();
        middleSection.row().expandX();
        middleSection.add(backToMenuButton).size(backToMenuButton.getWidth(), backToMenuButton.getHeight());

        stage.addActor(middleSection);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        super.drawBackground();

        stage.act(delta);
        stage.draw();

    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
        skin.dispose();
    }
}
