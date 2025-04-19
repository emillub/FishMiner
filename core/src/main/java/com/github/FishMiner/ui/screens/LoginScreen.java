package com.github.FishMiner.ui.screens;

import static com.github.FishMiner.ui.ports.out.ScreenType.LOGIN;

import java.io.ObjectInputFilter.Config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.github.FishMiner.common.Assets;
import com.github.FishMiner.common.Configuration;
import com.github.FishMiner.common.Logger;
import com.github.FishMiner.domain.GameEventBus;
import com.github.FishMiner.domain.events.data.AuthResponseEvent;
import com.github.FishMiner.domain.events.screenEvents.ChangeScreenEvent;
import com.github.FishMiner.domain.managers.ScreenManager;
import com.github.FishMiner.domain.ports.in.IGameEventListener;
import com.github.FishMiner.domain.ports.in.IGameScreen;
import com.github.FishMiner.domain.session.UserSession;
import com.github.FishMiner.ui.events.data.LoginRequestEvent;
import com.github.FishMiner.ui.ports.out.IGameContext;
import com.github.FishMiner.ui.ports.out.ScreenType;
import com.github.FishMiner.ui.events.data.RegisterUserRequest;
import com.github.FishMiner.ui.factories.ButtonFactory;


public class LoginScreen extends AbstractScreen implements IGameScreen {
    private static final String TAG = "LoginScreen";
    private TextField emailField;
    private TextField passwordField;
    private Label statusLabel;
    private boolean isRegistering;

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
        titleLabel.setFontScale(Configuration.getInstance().getLargeFontScale());

        statusLabel = new Label("", skin);
        statusLabel.setFontScale(Configuration.getInstance().getSmallFontScale());

        emailField = new TextField("", skin);
        emailField.setMessageText("Email");
        emailField.getStyle().font.getData().setScale(Configuration.getInstance().getSmallFontScale());

        passwordField = new TextField("", skin);
        passwordField.getStyle().font.getData().setScale(Configuration.getInstance().getSmallFontScale());
        passwordField.setMessageText("Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        passwordField.getStyle().font.getData().setScale(Configuration.getInstance().getSmallFontScale());

        TextButton loginButton = ButtonFactory.createTextButton("Login", ButtonFactory.ButtonSize.MEDIUM, () -> {
            String email = emailField.getText();
            String password = passwordField.getText();

            if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
                statusLabel.setText("Please enter both email and password.");
                return; // Don't proceed to Firebase
            }
            GameEventBus.getInstance().post(new LoginRequestEvent(email, password));
        });

        TextButton registerButton = ButtonFactory.createTextButton("Register", ButtonFactory.ButtonSize.MEDIUM, () -> {
            String email = emailField.getText();
            String password = passwordField.getText();

            if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
                statusLabel.setText("Please enter both email and password.");
                return; // Don't proceed to Firebase
            }
            GameEventBus.getInstance().post(new RegisterUserRequest(email, password));
        });

        TextButton backButton = ButtonFactory.createTextButton("Back", ButtonFactory.ButtonSize.MEDIUM, () -> {
            GameEventBus.getInstance().post(new ChangeScreenEvent(ScreenType.MENU));
        });

        // Add everything in correct order
        table.add(titleLabel).padBottom(Configuration.getInstance().getMediumPadding()).row();
        table.add(emailField).width(Configuration.getInstance().getScreenWidth() / 2)
                .height(emailField.getStyle().font.getData().lineHeight
                        + Configuration.getInstance().getMediumPadding())
                .padBottom(Configuration.getInstance().getMediumPadding()).row();
        table.add(passwordField).width(Configuration.getInstance().getScreenWidth() / 2)
                .padBottom(Configuration.getInstance().getLargePadding()).row();
        table.add(loginButton).size(loginButton.getWidth(), loginButton.getHeight())
                .padBottom(Configuration.getInstance().getSmallPadding()).row();
        table.add(registerButton).size(registerButton.getWidth(), registerButton.getHeight())
                .padBottom(Configuration.getInstance().getSmallPadding()).row();
        table.add(statusLabel).padBottom(Configuration.getInstance().getSmallPadding()).row();
        table.add(backButton).size(backButton.getWidth(), backButton.getHeight())
                .padBottom(Configuration.getInstance().getSmallPadding()).row();

        // --- Listeners ---
        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isRegistering = false;
                String email = emailField.getText();
                String password = passwordField.getText();
                GameEventBus.getInstance().post(new LoginRequestEvent(email, password));

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
                    Gdx.app.postRunnable(() -> {
                        UserSession.login(event.getEmail()); //set login
                        ScreenManager.getInstance().prepareNewScreen(ScreenType.MENU); //refresh the screen
                        ScreenManager.getInstance().switchScreenTo(ScreenType.MENU);
                    });
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
        super.drawBackground();
        stage.act(delta);
        stage.draw();
    }
}
