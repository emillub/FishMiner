package com.github.FishMiner.ui.views;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.FishMiner.infrastructure.Assets;
import com.github.FishMiner.infrastructure.Configuration;
import com.github.FishMiner.infrastructure.GameEventBus;
import com.github.FishMiner.ui.ports.out.domain.events.screenEvents.ChangeScreenEvent;
import com.github.FishMiner.ui.ports.out.domain.events.MusicEvent;
import com.github.FishMiner.ui.ports.out.domain.events.MusicEvent.MusicCommand;
import com.github.FishMiner.domain.managers.ScreenManager;
import com.github.FishMiner.domain.ports.in.ui.interfaces.IGameScreen;
import com.github.FishMiner.ui.factories.ButtonFactory;
import com.github.FishMiner.ui.ports.out.domain.interfaces.IGameContext;
import com.github.FishMiner.ui.ports.out.domain.enums.ScreenType;
import com.github.FishMiner.domain.model.session.UserSession;



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
                () -> {
                    GameEventBus.getInstance().post(new ChangeScreenEvent(ScreenType.SETTINGS));
                });

        ImageButton leaderboardButton = ButtonFactory.createImageButton(Assets.ButtonEnum.LEADERBOARD,
                        () -> GameEventBus.getInstance().post(new ChangeScreenEvent(ScreenType.LEADERBOARD)));

        ImageButton soundButton = ButtonFactory.createToggleButton(Configuration.getInstance().isMusicEnabled(),
                        Assets.ButtonEnum.MUTED, Assets.ButtonEnum.SOUND,
                        () -> {
                                GameEventBus.getInstance().post(new MusicEvent(MusicEvent.MusicCommand.TOGGLE_MUTE));
                });

        ImageButton tutorialButton = ButtonFactory.createImageButton(Assets.ButtonEnum.TUTORIAL,
                () -> GameEventBus.getInstance().post(new ChangeScreenEvent(ScreenType.TUTORIAL)));

        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().switchScreenTo(ScreenType.SETTINGS);
            }
        });


        stage.addActor(super.getTitleImage());

        Table middleSection = new Table();
        middleSection.setFillParent(true);
        middleSection.center().padTop(Configuration.getInstance().getLargePadding());

        TextButton playButtonText = ButtonFactory.createTextButton("PLAY", ButtonFactory.ButtonSize.LARGE, () -> {
                    GameEventBus.getInstance().post(new ChangeScreenEvent(ScreenType.PLAY));
                });
        middleSection.add(playButtonText).size(playButtonText.getWidth(), playButtonText.getHeight())
                .padBottom(Configuration.getInstance().getSmallPadding())
                .padTop(Configuration.getInstance().getMediumPadding()).top();
        middleSection.row().expandX();

        if (UserSession.isLoggedIn()) {
            TextButton logoutButton = ButtonFactory.createTextButton("LOGOUT", ButtonFactory.ButtonSize.MEDIUM, () -> {
                UserSession.logout();
                GameEventBus.getInstance().post(new ChangeScreenEvent(ScreenType.MENU));
            });
            middleSection.add(logoutButton).size(logoutButton.getWidth(), logoutButton.getHeight())
                    .padBottom(Configuration.getInstance().getSmallPadding()).row();
            Label loggedInLabel = new Label("Logged in as: " + UserSession.getCurrentUserEmail(), skin);
            loggedInLabel.setFontScale(Configuration.getInstance().getSmallFontScale());
            middleSection.add(loggedInLabel).center();
            ;
        } else {
            TextButton loginButton = ButtonFactory.createTextButton("LOGIN", ButtonFactory.ButtonSize.MEDIUM, () -> {
                        GameEventBus.getInstance().post(new ChangeScreenEvent(ScreenType.LOGIN));
                    });
            middleSection.add(loginButton).size(loginButton.getWidth(), loginButton.getHeight())
                    .padBottom(Configuration.getInstance().getSmallPadding());
        }
        stage.addActor(middleSection);

        Table bottomSectionTable = new Table();
        bottomSectionTable.bottom().padBottom(Configuration.getInstance().getLargePadding());
        bottomSectionTable.setFillParent(true);
        bottomSectionTable.add(soundButton).size(Configuration.getInstance().getBigIconWidth()).center()
                .padRight(Configuration.getInstance().getSmallPadding());
        bottomSectionTable.add(settingsButton).size(Configuration.getInstance().getBigIconWidth()).center()
                .padRight(Configuration.getInstance().getSmallPadding());
        bottomSectionTable.add(tutorialButton).size(Configuration.getInstance().getBigIconWidth()).center()
                .padRight(Configuration.getInstance().getSmallPadding());
        bottomSectionTable.add(leaderboardButton).size(Configuration.getInstance().getBigIconWidth()).center();
        stage.addActor(bottomSectionTable);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Assets.BLACK);
        super.drawBackground();
        stage.act(delta);
        stage.draw();
    }
}
