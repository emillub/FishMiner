package com.github.FishMiner.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.FishMiner.Configuration;
import com.github.FishMiner.FishMinerGame;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.github.FishMiner.data.ports.out.FirebaseAuthCallback;
import com.github.FishMiner.ui.controller.ScreenManager;


public class LoginScreen extends AbstractScreen {

    private TextField emailField;
    private TextField passwordField;
    private Label statusLabel;

    public LoginScreen() {
        super();
    }

    @Override
    public void show() {
        super.show();
        buildUI();
    }

    @Override
    protected void buildUI() {
        stage.clear();
        float scale = Configuration.getInstance().getUniformScale();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label titleLabel = new Label("Login", skin);
        titleLabel.setFontScale(3f * scale);

        emailField = new TextField("", skin);
        emailField.setMessageText("Email");
        emailField.getStyle().font.getData().setScale(1.5f * scale);

        passwordField = new TextField("", skin);
        passwordField.setMessageText("Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        passwordField.getStyle().font.getData().setScale(1.5f * scale);

        TextButton loginButton = new TextButton("Login", skin);
        loginButton.getLabel().setFontScale(1.5f * scale);


        TextButton registerButton = new TextButton("Register", skin);
        registerButton.getLabel().setFontScale(1.5f * scale);

        statusLabel = new Label("", skin);
        statusLabel.setFontScale(1.2f * scale);

        table.add(titleLabel).padBottom(40 * scale).row();
        table.add(emailField).width(500 * scale).height(80 * scale).padBottom(20 * scale).row();
        table.add(passwordField).width(500 * scale).height(80 * scale).padBottom(30 * scale).row();
        table.add(loginButton).width(400 * scale).height(100 * scale).padBottom(20 * scale).row();
        table.add(registerButton).width(400 * scale).height(100 * scale).padBottom(20 * scale).row();
        table.add(statusLabel).padTop(20 * scale).row();


        // --- Listeners ---
        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String email = emailField.getText();
                String password = passwordField.getText();

                FishMinerGame game = ScreenManager.getInstance().getGame();
                game.getFirebase().login(email, password, new FirebaseAuthCallback() {
                    @Override
                    public void onSuccess() {
                        Gdx.app.postRunnable(() -> {
                            statusLabel.setText("Login successful!");
                            ScreenManager.getInstance().startGamePressed();
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Gdx.app.postRunnable(() -> statusLabel.setText("Login failed: " + errorMessage));
                    }
                });
            }
        });

        registerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String email = emailField.getText();
                String password = passwordField.getText();

                if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
                    statusLabel.setText("Please enter both email and password.");
                    return; // Don't proceed to Firebase
                }

                FishMinerGame game = ScreenManager.getInstance().getGame();
                game.getFirebase().register(email, password, new FirebaseAuthCallback() {
                    @Override
                    public void onSuccess() {
                        Gdx.app.postRunnable(() -> statusLabel.setText("Registration successful!"));
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Gdx.app.postRunnable(() -> statusLabel.setText("Registration failed: " + errorMessage));
                    }
                });
            }
        });
    }


    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta);
        stage.draw();
    }
}
