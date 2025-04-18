package com.github.FishMiner.ui.screens;


import java.io.ObjectInputFilter.Config;

import org.w3c.dom.Text;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.FishMiner.common.Assets;
import com.github.FishMiner.common.Configuration;
import com.github.FishMiner.domain.GameEventBus;
import com.github.FishMiner.domain.events.screenEvents.ChangeScreenEvent;
import com.github.FishMiner.domain.events.soundEvents.MusicEvent;
import com.github.FishMiner.domain.events.soundEvents.MusicEvent.MusicCommand;
import com.github.FishMiner.domain.managers.ScreenManager;
import com.github.FishMiner.domain.ports.in.IGameScreen;
import com.github.FishMiner.ui.factories.ButtonFactory;
import com.github.FishMiner.ui.ports.out.IGameContext;
import com.github.FishMiner.ui.ports.out.ScreenType;


public class MenuScreen extends AbstractScreen implements IGameScreen {
    public MenuScreen(IGameContext gameContext) {
        super(gameContext);
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(stage);
        GameEventBus.getInstance().post(new MusicEvent(MusicCommand.PLAY_BACKGROUND));

        ImageButton settingsButton = ButtonFactory.createImageButton(Assets.ButtonEnum.SETTINGS,
                        () -> ScreenManager.getInstance().switchScreenTo(ScreenType.SETTINGS));

        ImageButton leaderboardButton = ButtonFactory.createImageButton(Assets.ButtonEnum.LEADERBOARD,
                        () -> GameEventBus.getInstance().post(new ChangeScreenEvent(ScreenType.LEADERBOARD)));

        ImageButton soundButton = ButtonFactory.createToggleButton(Configuration.getInstance().isMusicEnabled(),
                        Assets.ButtonEnum.MUTED, Assets.ButtonEnum.SOUND,
                        () -> {
                                GameEventBus.getInstance().post(new MusicEvent(MusicEvent.MusicCommand.TOGGLE_MUTE));
                        });

        TextButton playButtonText = ButtonFactory.createTextButton("PLAY",
                        Configuration.getInstance().getSmallFontScale(), skin, () -> {
                                GameEventBus.getInstance().post(new ChangeScreenEvent(ScreenType.PLAY));
        });

        TextButton loginButton = ButtonFactory.createTextButton("LOGIN",
                        Configuration.getInstance().getSmallFontScale(), skin, () -> {
                                GameEventBus.getInstance().post(new ChangeScreenEvent(ScreenType.LOGIN));
        });

        LabelStyle labelStyle = new LabelStyle(Assets.TITLE_FONT, Assets.DARK_BROWN);
        Label titleLabel = new Label("Fish Miner", labelStyle);
        titleLabel.setFontScale(Configuration.getInstance().getSmallFontScale());

        Table topSectionTable = new Table();
        topSectionTable.setFillParent(true);
        topSectionTable.top();

        titleLabel.setPosition(
                        Configuration.getInstance().getScreenWidth() / 2
                                        - titleLabel.getWidth() * titleLabel.getFontScaleX() / 2,
                        Configuration.getInstance().getScreenHeight()
                                        * Configuration.getInstance().getOceanHeightPercentage() +
                                        titleLabel.getHeight() * titleLabel.getFontScaleY() / 2);

        stage.addActor(titleLabel);

        Table middleSection = new Table();
        middleSection.setFillParent(true);
        middleSection.center();

        middleSection.add(playButtonText).size(playButtonText.getWidth(), playButtonText.getHeight())
                        .padBottom(Configuration.getInstance().getSmallPadding()).top();
        middleSection.row().expandX();
        middleSection.add(loginButton).size(loginButton.getWidth(), loginButton.getHeight())
                        .padBottom(Configuration.getInstance().getSmallPadding());
        stage.addActor(middleSection);

        Table bottomSectionTable = new Table();
        bottomSectionTable.bottom();
        bottomSectionTable.setFillParent(true);
        bottomSectionTable.add(soundButton).size(Configuration.getInstance().getIconWidth())
                        .padBottom(Configuration.getInstance().getLargePadding()).center();
        stage.addActor(bottomSectionTable);
    }

    @Override
    public void render(float delta) {
            ScreenUtils.clear(Assets.BLACK);
        super.drawBackground();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void hide() {
        super.hide();
        stage.clear();
    }
}
