package com.github.FishMiner.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.FishMiner.FishMinerGame;
import com.github.FishMiner.data.services.FirebaseAuthCallback;
import com.github.FishMiner.ui.controller.ScreenManager;

public class LoginScreen extends AbstractScreen {

    private final String initialMessage;
    private final boolean[] isLoginMode = {true};

    private TextField emailField;
    private TextField passwordField;
    private TextField confirmPasswordField;
    private Label statusLabel;
    private Label titleLabel;
    private TextButton submitButton;
    private Label toggleLink;
    private Table dynamicContainer;

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
        stage.getRoot().setColor(0.1f, 0.2f, 0.3f, 1); // Background color

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        initializeUIElements();
        addToggleBehavior();
        addSubmitBehavior();

        table.add(titleLabel).padBottom(40).row();
        table.add(emailField).width(500).height(80).padBottom(20).row();
        table.add(passwordField).width(500).height(80).padBottom(20).row();
        table.add(dynamicContainer).row();

        TextButton backButton = createStyledButton("Back to Menu");
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.getInstance().showMenu();
            }
        });

        table.add(backButton).right().padRight(50).width(180).height(50).padBottom(30).row();

        renderLoginLayout(); // Initial layout
    }

    private void initializeUIElements() {
        titleLabel = new Label("LOGIN", skin, "subtitle");
        titleLabel.setFontScale(2f);

        emailField = createTextField("  Email");
        passwordField = createTextField("  Password", true);
        confirmPasswordField = createTextField("Confirm Password", true);
        confirmPasswordField.setVisible(false);

        statusLabel = new Label(initialMessage, skin);
        statusLabel.setFontScale(1.2f);

        submitButton = createStyledButton("Login");
        submitButton.getLabel().setFontScale(1.5f);

        toggleLink = new Label("Don't have an account? Register", skin);
        toggleLink.setColor(Color.LIGHT_GRAY);
        toggleLink.setFontScale(1f);

        dynamicContainer = new Table();
    }

    private TextField createTextField(String placeholder) {
        return createTextField(placeholder, false);
    }

    private TextField createTextField(String placeholder, boolean isPassword) {
        TextField field = new TextField("", skin);
        field.setMessageText(placeholder);
        field.getStyle().font.getData().setScale(1.5f);
        field.getStyle().messageFontColor = Color.LIGHT_GRAY;
        field.setAlignment(Align.left);
        if (isPassword) {
            field.setPasswordMode(true);
            field.setPasswordCharacter('*');
        }
        return field;
    }

    private void addToggleBehavior() {
        toggleLink.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isLoginMode[0] = !isLoginMode[0];
                renderLoginLayout();
            }
        });
    }

    private void renderLoginLayout() {
        titleLabel.setText(isLoginMode[0] ? "LOGIN" : "REGISTER");
        submitButton.setText(isLoginMode[0] ? "Login" : "Register");
        toggleLink.setText(isLoginMode[0]
            ? "Don't have an account? Register"
            : "Already have an account? Log in");
        statusLabel.setText(initialMessage);

        confirmPasswordField.setVisible(!isLoginMode[0]); // <-- add this

        dynamicContainer.clear();

        if (!isLoginMode[0]) {
            dynamicContainer.add(confirmPasswordField).width(500).height(80).padBottom(20).row();
        }

        dynamicContainer.add(toggleLink).left().padBottom(30).row();
        dynamicContainer.add(statusLabel).padTop(20).row();
        dynamicContainer.add(submitButton).width(400).height(100).padBottom(20).row();
    }


    private void addSubmitBehavior() {
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
                    game.getFirebase().login(email, password, createAuthCallback("Login"));
                } else {
                    game.getFirebase().register(email, password, createAuthCallback("Register"));
                }
            }
        });
    }

    private FirebaseAuthCallback createAuthCallback(String mode) {
        return new FirebaseAuthCallback() {
            @Override
            public void onSuccess() {
                Gdx.app.postRunnable(() -> {
                    if (mode.equals("Login")) {
                        statusLabel.setText("Login successful!");
                        ScreenManager.getInstance().showMenu();
                    } else {
                        ScreenManager.getInstance().setLoginScreen(
                            new LoginScreen("Registration successful! Please log in.")
                        );
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                Gdx.app.postRunnable(() ->
                    statusLabel.setText(mode + " failed: " + errorMessage));
            }
        };
    }

    private TextButton createStyledButton(String text) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = skin.newDrawable("white", new Color(0.2f, 0.4f, 0.8f, 1f));      // Normal
        style.down = skin.newDrawable("white", new Color(0.15f, 0.3f, 0.6f, 1f));   // Pressed
        style.over = skin.newDrawable("white", new Color(0.25f, 0.5f, 1f, 1f));     // Hover
        style.font = skin.getFont("default");
        style.fontColor = Color.WHITE;

        TextButton button = new TextButton(text, style);
        button.getLabel().setFontScale(1.5f);
        return button;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.4f, 1);
        stage.act(delta);
        stage.draw();
    }
}
