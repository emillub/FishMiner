package com.github.FishMiner.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.FishMiner.FishMinerGame;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.github.FishMiner.data.services.FirebaseAuthCallback;
import com.github.FishMiner.ui.controller.ScreenManager;

public class LoginScreen extends AbstractScreen {

    private TextField emailField;
    private TextField passwordField;
    private TextField confirmPasswordField;
    private Label statusLabel;

    private final String initialMessage;

    public LoginScreen() {
        this("");
    }

    public LoginScreen(String initialMessage) {
        super();
        this.initialMessage = initialMessage;
    }
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        final boolean[] isLoginMode = {true};

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        stage.getRoot().setColor(0.1f, 0.2f, 0.3f, 1);

        Label titleLabel = new Label("LOGIN", skin, "subtitle");
        titleLabel.setFontScale(2f);

        emailField = new TextField("", skin);
        emailField.setMessageText("Email");
        emailField.getStyle().font.getData().setScale(1.5f);
        emailField.getStyle().messageFontColor = Color.LIGHT_GRAY;
        emailField.setAlignment(Align.left);

        passwordField = new TextField("", skin);
        passwordField.setMessageText("Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        passwordField.getStyle().font.getData().setScale(1.5f);
        passwordField.getStyle().messageFontColor = Color.LIGHT_GRAY;
        passwordField.setAlignment(Align.left);

        confirmPasswordField = new TextField("", skin);
        confirmPasswordField.setMessageText("Confirm Password");
        confirmPasswordField.setPasswordMode(true);
        confirmPasswordField.setPasswordCharacter('*');
        confirmPasswordField.getStyle().font.getData().setScale(1.5f);
        confirmPasswordField.getStyle().messageFontColor = Color.LIGHT_GRAY;
        confirmPasswordField.setAlignment(Align.left);

        statusLabel = new Label(initialMessage, skin);
        statusLabel.setFontScale(1.2f);

        TextButton submitButton = new TextButton("Login", skin, "blue-accent");
        submitButton.getLabel().setFontScale(1.5f);

        Label toggleLink = new Label("Don't have an account? Register", skin);

        toggleLink.setColor(Color.LIGHT_GRAY);
        toggleLink.setFontScale(1f);

        TextButton backButton = new TextButton("Back to Menu", skin, "blue-accent");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().showMenu();
            }
        });

        Table dynamicContainer = new Table();

        toggleLink.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isLoginMode[0] = !isLoginMode[0];

                titleLabel.setText(isLoginMode[0] ? "LOGIN" : "REGISTER");
                submitButton.setText(isLoginMode[0] ? "Login" : "Register");
                toggleLink.setText(isLoginMode[0]
                    ? "Don't have an account? Register"
                    : "Already have an account? Log in");

                statusLabel.setText("");

                // Rebuild dynamic content
                dynamicContainer.clear();

                if (!isLoginMode[0]) {
                    dynamicContainer.add(confirmPasswordField).width(500).height(80).padBottom(20).row();
                }

                dynamicContainer.add(toggleLink).left().padBottom(30).padLeft(50).row();
                dynamicContainer.add(statusLabel).padTop(20).row();
                dynamicContainer.add(submitButton).width(400).height(100).padBottom(20).row();
            }
        });


        // Static layout
        table.add(titleLabel).padBottom(40).row();
        table.add(emailField).width(500).height(80).padBottom(20).row();
        table.add(passwordField).width(500).height(80).padBottom(20).row();
        table.add(dynamicContainer).row();
        table.add(backButton).right().padRight(50).width(180).height(50).padBottom(30).row();

        // Initial login layout
        dynamicContainer.clear();
        dynamicContainer.add(toggleLink).left().padBottom(30).padLeft(50).row();
        dynamicContainer.add(statusLabel).padTop(20).row();
        dynamicContainer.add(submitButton).width(400).height(100).padBottom(20).row();

        submitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String email = emailField.getText();
                String password = passwordField.getText();
                String confirmPassword = confirmPasswordField.getText();

                if (email.isEmpty() || password.isEmpty() || (!isLoginMode[0] && confirmPassword.isEmpty())) {
                    statusLabel.setText("Please fill in all fields.");
                    return;
                }

                if (!isLoginMode[0] && !password.equals(confirmPassword)) {
                    statusLabel.setText("Passwords do not match.");
                    return;
                }

                FishMinerGame game = ScreenManager.getInstance().getGame();
                if (isLoginMode[0]) {
                    game.getFirebase().login(email, password, new FirebaseAuthCallback() {
                        @Override
                        public void onSuccess() {
                            Gdx.app.postRunnable(() -> {
                                statusLabel.setText("Login successful!");
                                ScreenManager.getInstance().showMenu();
                            });
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Gdx.app.postRunnable(() -> statusLabel.setText("Login failed: " + errorMessage));
                        }
                    });
                } else {
                    game.getFirebase().register(email, password, new FirebaseAuthCallback() {
                        @Override
                        public void onSuccess() {
                            Gdx.app.postRunnable(() -> {
                                ScreenManager.getInstance().setLoginScreen(
                                    new LoginScreen("Registration successful! Please log in.")
                                );
                            });
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Gdx.app.postRunnable(() -> statusLabel.setText("Registration failed: " + errorMessage));
                        }
                    });
                }
            }
        });
    }


    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.4f, 1);
        stage.act(delta);
        stage.draw();
    }
}
