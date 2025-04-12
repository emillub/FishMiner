package com.github.FishMiner.ui.screens;

import static com.github.FishMiner.ui.ports.out.ScreenType.LOGIN;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.github.FishMiner.common.Logger;
import com.github.FishMiner.domain.GameEventBus;
import com.github.FishMiner.domain.events.data.AuthResponseEvent;
import com.github.FishMiner.domain.managers.ScreenManager;
import com.github.FishMiner.domain.ports.in.IGameEventListener;
import com.github.FishMiner.domain.ports.in.IGameScreen;
import com.github.FishMiner.ui.events.data.LoginRequestEvent;
import com.github.FishMiner.ui.ports.out.IGameContext;
import com.github.FishMiner.ui.ports.out.ScreenType;
import com.github.FishMiner.ui.events.data.RegisterUserRequest;


public class LoginScreen extends AbstractScreen implements IGameScreen {
    private static final String TAG = "LoginScreen";
    private TextField emailField;
    private TextField passwordField;
    private Label statusLabel;

    public LoginScreen(IGameContext gameContext) {
        super(gameContext);
        screenType = LOGIN;
        GameEventBus.getInstance().register(getAuthResponseListener());
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(stage);


        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label titleLabel = new Label("Login", skin);
        titleLabel.setFontScale(2f);

        emailField = new TextField("", skin);
        emailField.setMessageText("Email");
        emailField.getStyle().font.getData().setScale(1.5f);

        passwordField = new TextField("", skin);
        passwordField.setMessageText("Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        passwordField.getStyle().font.getData().setScale(1.5f);

        TextButton loginButton = new TextButton("Login", skin);
        loginButton.getLabel().setFontScale(1.5f);

        TextButton registerButton = new TextButton("Register", skin);
        registerButton.getLabel().setFontScale(1.5f);

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().switchScreenTo(ScreenType.MENU);
            }
        });

        statusLabel = new Label("", skin);
        statusLabel.setFontScale(1.2f);

        // Add everything in correct order
        table.add(titleLabel).padBottom(40).row();
        table.add(emailField).width(500).height(80).padBottom(20).row();
        table.add(passwordField).width(500).height(80).padBottom(30).row();
        table.add(loginButton).width(400).height(100).padBottom(20).row();
        table.add(registerButton).width(400).height(100).padBottom(20).row();
        table.add(statusLabel).padTop(20).row();
        table.add(backButton).width(400).height(100).padBottom(20).row();

        // --- Listeners ---
        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String email = emailField.getText();
                String password = passwordField.getText();
                GameEventBus.getInstance().post(new LoginRequestEvent(email, password));

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
                GameEventBus.getInstance().post(new RegisterUserRequest(email, password));
            }
        });
    }

    /**
     * Returns an event listener for login/registration responses.
     */
    public IGameEventListener<AuthResponseEvent> getAuthResponseListener() {
        return new IGameEventListener<AuthResponseEvent>() {
            @Override
            public void onEvent(AuthResponseEvent event) {
                if (event.isHandled()) return;
                if (event.wasSuccessful()) {
                    event.getEmail();
                    Gdx.app.postRunnable(() -> statusLabel.setText("Registered Successful. User: " + event.getEmail()));
                } else {
                    String errorMessage = event.getError();
                    Logger.getInstance().error(TAG, "Registration failed: " + errorMessage);
                    Gdx.app.postRunnable(() -> statusLabel.setText("Registration failed: " + errorMessage));

                }
            }
            @Override
            public Class<AuthResponseEvent> getEventType() {
                return AuthResponseEvent.class;
            }
        };
    }


    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta);
        stage.draw();
    }
}
