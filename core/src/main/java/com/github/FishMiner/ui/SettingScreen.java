package com.github.FishMiner.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.FishMinerGame;
import com.github.FishMiner.ui.controller.ScreenManager;

public class SettingScreen extends AbstractScreen {

    private CheckBox soundCheckBox;
    private Slider volumeSlider;
    public SettingScreen() {
        super();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // Title Label
        Label titleLabel = new Label("Settings", skin);
        titleLabel.setFontScale(3.5f);
        rootTable.add(titleLabel).pad(30);
        rootTable.row();
        soundCheckBox = new CheckBox(" Enable Sound", skin);
        soundCheckBox.getLabel().setFontScale(2f);
        soundCheckBox.setChecked(Configuration.getInstance().isSoundEnabled());
        soundCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean isEnabled = soundCheckBox.isChecked();
                Configuration.getInstance().setSoundEnabled(isEnabled);

                Music backgroundMusic = FishMinerGame.getBackgroundMusic();
                Music playMusic = FishMinerGame.getPlayMusic();

                if (isEnabled) {
                    // Resume whichever one is currently relevant
                    if (playMusic.isPlaying()) {
                        playMusic.play();
                    } else {
                        backgroundMusic.play();
                    }
                } else {
                    backgroundMusic.pause();
                    playMusic.pause();
                }
            }
        });

        rootTable.add(soundCheckBox).pad(10);
        rootTable.row();

        Label volumeLabel = new Label("Music Volume", skin);
        volumeLabel.setFontScale(2f);
        rootTable.add(volumeLabel).pad(10);
        rootTable.row();

        volumeSlider = new Slider(0f, 1f, 0.1f, false, skin);
        volumeSlider.setValue(Configuration.getInstance().getMusicVolume());
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float volume = volumeSlider.getValue();
                Configuration.getInstance().setMusicVolume(volume);
                FishMinerGame.getBackgroundMusic().setVolume(volume);
                FishMinerGame.getPlayMusic().setVolume(volume);
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
                ScreenManager.getInstance().showMenu();
            }
        });
        rootTable.add(backButton).pad(10);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        stage.act(delta);
        stage.draw();
    }
}
