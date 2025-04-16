package com.github.FishMiner.ui.screens;

import static com.github.FishMiner.domain.events.soundEvents.MusicEvent.MusicCommand.ADJUST_VOLUME;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.FishMiner.common.Configuration;
import com.github.FishMiner.domain.GameEventBus;
import com.github.FishMiner.domain.events.soundEvents.MusicEvent;
import com.github.FishMiner.domain.managers.ScreenManager;
import com.github.FishMiner.domain.ports.in.IGameScreen;
import com.github.FishMiner.ui.ports.out.IGameContext;
import com.github.FishMiner.ui.ports.out.ScreenType;

public class SettingScreen extends AbstractScreen implements IGameScreen {

    public SettingScreen(IGameContext gameContext) {
        super(gameContext);
        screenType = ScreenType.SETTINGS;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        stage.clear(); // Clear the stage to avoid adding duplicate actors
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // Title Label
        Label titleLabel = new Label("Settings", skin);
        titleLabel.setFontScale(3.5f);
        rootTable.add(titleLabel).pad(30);
        rootTable.row();

        Label volumeLabel = new Label("Music Volume", skin);
        volumeLabel.setFontScale(2f);
        rootTable.add(volumeLabel).pad(10);
        rootTable.row();

        Slider volumeSlider = new Slider(0f, 1f, 0.1f, false, skin);
        volumeSlider.setValue(Configuration.getInstance().getMusicVolume());
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MusicEvent musicEvent = new MusicEvent(ADJUST_VOLUME);
                musicEvent.setVolume(volumeSlider.getValue());
                GameEventBus.getInstance().post(musicEvent);
            }
        });
        rootTable.add(volumeSlider).width(200).pad(10);
        rootTable.row();

        TextButton backButton = new TextButton("Back to Menu", skin);
        backButton.getLabel().setColor(Color.WHITE);
        backButton.setColor(Color.BLACK);
        backButton.getLabel().setFontScale(2f);

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().switchScreenTo(ScreenType.MENU);
            }
        });
        rootTable.add(backButton).pad(10);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        super.drawBackground();
        stage.act(delta);
        stage.draw();
    }
}
