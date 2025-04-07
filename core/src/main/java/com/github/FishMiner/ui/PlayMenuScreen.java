package com.github.FishMiner.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.github.FishMiner.FishMinerGame;

/**
 * Dette er bare et midlertidig overlay jesper lagde for å ha noe å forholde seg til.
 * Det er bare å endre denne som man vil.
 */
public class PlayMenuScreen extends AbstractScreen {
    private Window overlay;
    private Stage parentStage; // Reference to the PlayScreen's stage

    /**
     * Constructor accepts the parent stage to which this overlay will be added.
     */
    public PlayMenuScreen(Stage parentStage) {
        this.parentStage = parentStage;
        createOverlay();
    }

    /**
     * Creates the overlay window with Resume and Exit buttons.
     */
    private void createOverlay() {
        overlay = new Window("Pause Menu", skin, "default");
        // Set the overlay size (here, half the width and height of the parent stage)
        overlay.setSize(parentStage.getWidth() * 0.5f, parentStage.getHeight() * 0.5f);
        overlay.setPosition(
            MathUtils.roundPositive((parentStage.getWidth() - overlay.getWidth()) / 2f),
            MathUtils.roundPositive((parentStage.getHeight() - overlay.getHeight()) / 2f)
        );
        overlay.pad(10);

        // Create the Resume button
        TextButton resumeButton = new TextButton("Resume", skin);
        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Remove the overlay to resume gameplay
                overlay.remove();
            }
        });
        overlay.add(resumeButton).pad(10);
        overlay.row();

        // Create the Exit button
        TextButton exitButton = new TextButton("Exit", skin);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Exit the PlayState and switch to the main MenuScreen
                FishMinerGame game = (FishMinerGame) Gdx.app.getApplicationListener();
                game.setScreen(new MenuScreen());
            }
        });
        overlay.add(exitButton).pad(10);
    }

    /**
     * Returns the overlay window actor so it can be added to the PlayScreen's stage.
     */
    public Window getOverlay() {
        return overlay;
    }

    // The following methods are not used as this class functions as an overlay.
    @Override
    public void show() { }

    @Override
    public void render(float delta) { }

    @Override
    public void resize(int width, int height) { }

    @Override
    protected void buildUI() {
        //No UI components needed for this screen, I guess?
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        overlay.clear();
    }
}
