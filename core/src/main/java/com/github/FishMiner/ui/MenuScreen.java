package com.github.FishMiner.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.FishMinerGame;
import com.github.FishMiner.domain.ecs.components.InventoryComponent;
import com.github.FishMiner.ui.controller.ScreenManager;

public class MenuScreen extends AbstractScreen {

    public MenuScreen() {
        super();
    }

    @Override
    public void show() {
        super.show();
        FishMinerGame.playBackgroundMusic();
        buildUI();
    }

    @Override
    protected void buildUI() {
        stage.clear();

        Configuration config = Configuration.getInstance();
        float scale = config.getUniformScale();

        float fontScale = (!config.isAndroid() || config.isPortrait() ? 2.5f * scale : 2f * scale);
        float buttonWidth = 600 * scale;
        float buttonHeight = 100 * fontScale;
        float spacing = 30 * scale;

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.center().top().padTop(150 * scale);
        stage.addActor(rootTable);

        TextButton settingsButton = new TextButton("Settings", skin);
        settingsButton.getLabel().setFontScale(fontScale);
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().setSettingScreen(new SettingScreen());
            }
        });
        TextButton loginButton = new TextButton("Login", skin);
        loginButton.getLabel().setFontScale(fontScale);
        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().setLoginScreen(new LoginScreen());
            }
        });

        TextButton playButton = new TextButton("Play", skin);
        playButton.getLabel().setFontScale(fontScale);
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().startGamePressed();
            }
        });

        TextButton testButton = new TextButton("TEST", skin);
        testButton.getLabel().setFontScale(fontScale);
        testButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                InventoryComponent inventory = new InventoryComponent();
                inventory.money = 200;
                ScreenManager.getInstance().showLevelCompleteScreen(1, inventory);
            }
        });

        // Layout order
        rootTable.add(loginButton).width(buttonWidth).height(buttonHeight).padBottom(spacing).row();
        rootTable.add(playButton).width(buttonWidth).height(buttonHeight).padBottom(spacing).row();
        rootTable.add(settingsButton).width(buttonWidth).height(buttonHeight).padBottom(spacing).row();
        rootTable.add(testButton).width(buttonWidth).height(buttonHeight).padBottom(spacing).row();

        Table bottomTable = new Table();
        bottomTable.setFillParent(true);
        bottomTable.bottom().padBottom(30 * scale);
        stage.addActor(bottomTable);

        Label creditLabel = new Label("© 2025 FishMiner Studios", skin);
        creditLabel.setFontScale(fontScale * 0.8f);
        bottomTable.add(creditLabel).center();

        /* If needed later!
        Image logo = new Image(new Texture(Gdx.files.internal("ui/logo.png")));
        bottomTable.add(logo).size(200 * scale, 80 * scale).padBottom(10 * scale).row();
        bottomTable.add(creditLabel).center();

         */

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        stage.act(delta);
        stage.draw();
    }
}
