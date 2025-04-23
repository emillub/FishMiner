package com.github.FishMiner.ui.screens;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.FishMiner.infrastructure.Assets;
import com.github.FishMiner.infrastructure.Configuration;
import com.github.FishMiner.infrastructure.GameEventBus;
import com.github.FishMiner.ui.ports.out.domain.events.screenEvents.ChangeScreenEvent;
import com.github.FishMiner.domain.ports.in.ui.interfaces.IGameScreen;
import com.github.FishMiner.ui.factories.ButtonFactory;
import com.github.FishMiner.ui.ports.out.domain.interfaces.IGameContext;
import com.github.FishMiner.ui.ports.out.domain.enums.ScreenType;

public class TutorialScreen extends AbstractScreen implements IGameScreen, GestureListener {
    private int currentPage = 0;
    private final List<Texture> tutorialTextures;
    private Image tutorialImage;
    private Table rootTable;

    public TutorialScreen(IGameContext gameContext) {
        super(gameContext);
        tutorialTextures = Assets.getInstance().getTutorialTextures();
    }

    @Override
    public void show() {
        super.show();
        GestureDetector gestureDetector = new GestureDetector(this);
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage); // Allow stage to process input for UI elements
        inputMultiplexer.addProcessor(gestureDetector); // Add gesture detector for swiping
        Gdx.input.setInputProcessor(inputMultiplexer);

        rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        Label titleLabel = new Label("HOW TO PLAY", skin);
        titleLabel.setStyle(new Label.LabelStyle(Assets.DEFAULT_FONT, Assets.TITLE_COLOR));
        titleLabel.setFontScale(Configuration.getInstance().getLargeFontScale());
        rootTable.add(titleLabel).expandX().padTop(Configuration.getInstance().getMediumPadding()).row();

        Label descriptionLabel = new Label("Swipe left or right", skin);
        descriptionLabel.setStyle(new Label.LabelStyle(Assets.DEFAULT_FONT, Assets.TITLE_COLOR));
        descriptionLabel.setFontScale(Configuration.getInstance().getSmallFontScale());
        rootTable.add(descriptionLabel).expandX().padTop(Configuration.getInstance().getMediumPadding()).row();

        updateTutorialImage();

        TextButton backButton = ButtonFactory.createTextButton("Back to Menu",
                ButtonFactory.ButtonSize.MEDIUM, () -> {
                    GameEventBus.getInstance().post(new ChangeScreenEvent(ScreenType.MENU));
                });

        rootTable.row();
        rootTable.add(backButton).size(backButton.getWidth(), backButton.getHeight()).center()
                .padTop(Configuration.getInstance().getSmallPadding()).padBottom(Configuration.getInstance().getSmallPadding());
    }

    private void updateTutorialImage() {
        if (tutorialImage == null) {
            tutorialImage = new Image(tutorialTextures.get(currentPage));
            tutorialImage.setScaling(Scaling.fit);
            rootTable.add(tutorialImage).expand().center();
        } else {
            tutorialImage.setDrawable(new Image(tutorialTextures.get(currentPage)).getDrawable());
            tutorialImage.setScaling(Scaling.fit);
        }

        // Ensure the tutorialImage is added to the rootTable
        if (!rootTable.getChildren().contains(tutorialImage, true)) {
            rootTable.add(tutorialImage).expand().center();
        }
    }

    @Override
    public void hide() {
        super.hide();
        currentPage = 0;
    }


    @Override
public boolean fling(float velocityX, float velocityY, int button) {

        if (Math.abs(velocityX) > Math.abs(velocityY)) {
            if (velocityX > 0) {
                currentPage--;
                if (currentPage < 0) {
                    currentPage = tutorialTextures.size() - 1;
                }
            } else {
                currentPage++;
                if (currentPage >= tutorialTextures.size()) {
                    currentPage = 0;
                }
            }
            updateTutorialImage();
            return true;
        }
    return false;
}


    @Override
    public void render(float delta) {
        ScreenUtils.clear(Assets.BACKGROUND_COLOR);
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }


    // Unused GestureListener methods
    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(com.badlogic.gdx.math.Vector2 initialPointer1, com.badlogic.gdx.math.Vector2 initialPointer2,
                         com.badlogic.gdx.math.Vector2 pointer1, com.badlogic.gdx.math.Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {
    }
}
