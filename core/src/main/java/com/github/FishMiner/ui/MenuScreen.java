package com.github.FishMiner.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.FishMiner.FishMinerGame;

public class MenuScreen extends AbstractScreen {

    public MenuScreen() {
        super();
    }

    @Override
    public void show() {
        super.show();

        // Create a window to serve as the main menu container
        Window window = new Window("Main Menu", skin, "default");
        window.defaults().pad(10f);

        // Create a table for button layout
        Table buttonTable = new Table();
        buttonTable.defaults().pad(10f).fillX().uniformX();

        // Create and add the Play button
        TextButton playButton = new TextButton("Play", skin);
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Start the game by switching to GameScreen
                ((FishMinerGame) Gdx.app.getApplicationListener()).setScreen(new PlayScreen());
            }
        });
        buttonTable.add(playButton);
        buttonTable.row();

        // Create and add the Exit button
        TextButton exitButton = new TextButton("Exit", skin);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Exit the application
                Gdx.app.exit();
            }
        });
        buttonTable.add(exitButton);

        // Add the button table to the window
        window.add(buttonTable).expand().fill();
        window.pack();

        // Center the window on the stage
        window.setPosition(
            MathUtils.roundPositive(stage.getWidth() / 2f - window.getWidth() / 2f),
            MathUtils.roundPositive(stage.getHeight() / 2f - window.getHeight() / 2f)
        );

        // Add a fade-in effect for a smoother transition
        window.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(1f)));

        stage.addActor(window);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        stage.act(delta);
        stage.draw();
    }
}
