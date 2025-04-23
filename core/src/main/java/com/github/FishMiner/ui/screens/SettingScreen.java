package com.github.FishMiner.ui.screens;

import static com.github.FishMiner.ui.ports.out.domain.events.MusicEvent.MusicCommand.ADJUST_VOLUME;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.FishMiner.infrastructure.Assets;
import com.github.FishMiner.infrastructure.Configuration;
import com.github.FishMiner.infrastructure.GameEventBus;
import com.github.FishMiner.ui.ports.out.domain.events.screenEvents.ChangeScreenEvent;
import com.github.FishMiner.ui.ports.out.domain.events.MusicEvent;
import com.github.FishMiner.domain.ports.in.ui.interfaces.IGameScreen;
import com.github.FishMiner.ui.factories.ButtonFactory;
import com.github.FishMiner.ui.ports.out.domain.interfaces.IGameContext;
import com.github.FishMiner.ui.ports.out.domain.enums.ScreenType;

public class SettingScreen extends AbstractScreen implements IGameScreen {

    public SettingScreen(IGameContext gameContext) {
        super(gameContext);
        screenType = ScreenType.SETTINGS;
    }

    @Override
    public void show() {
        super.show();
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        float menuWidth = Configuration.getInstance().getQuarterScreenWidth();

        // Title Label
        Label titleLabel = new Label("Settings", skin);
        titleLabel.setFontScale(Configuration.getInstance().getLargeFontScale());
        rootTable.add(titleLabel).pad(Configuration.getInstance().getLargePadding());
        rootTable.row();

        Label volumeLabel = new Label("Music Volume", skin);
        volumeLabel.setFontScale(Configuration.getInstance().getMediumFontScale());
        rootTable.add(volumeLabel).pad(Configuration.getInstance().getSmallPadding());
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
        rootTable.add(volumeSlider).width(menuWidth).padBottom(Configuration.getInstance().getSmallPadding());
        rootTable.row();

        TextButton backButton = ButtonFactory.createTextButton("Back to Menu",
                ButtonFactory.ButtonSize.MEDIUM, () -> {
                    GameEventBus.getInstance().post(new ChangeScreenEvent(ScreenType.MENU));
                });

        rootTable.add(backButton).size(backButton.getWidth(), backButton.getHeight())
                .pad(Configuration.getInstance().getSmallPadding());
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Assets.BLACK);
        super.drawBackground();
        stage.act(delta);
        stage.draw();
    }
}
