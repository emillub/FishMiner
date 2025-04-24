package com.github.FishMiner.ui.views;

import static com.github.FishMiner.ui.ports.out.domain.enums.ScreenType.LOGIN;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.FishMiner.infrastructure.Configuration;
import com.github.FishMiner.infrastructure.Logger;
import com.github.FishMiner.infrastructure.GameEventBus;
import com.github.FishMiner.domain.ports.in.data.events.AuthResponseEvent;
import com.github.FishMiner.ui.ports.out.domain.events.screenEvents.ChangeScreenEvent;
import com.github.FishMiner.domain.managers.ScreenManager;
import com.github.FishMiner.infrastructure.ports.in.IGameEventListener;
import com.github.FishMiner.domain.ports.in.ui.interfaces.IGameScreen;
import com.github.FishMiner.domain.model.session.UserSession;
import com.github.FishMiner.domain.ports.in.data.events.LoginRequestEvent;
import com.github.FishMiner.domain.ports.in.data.events.RegisterUserRequest;
import com.github.FishMiner.ui.factories.ButtonFactory;
import com.github.FishMiner.ui.factories.ButtonFactory.ButtonSize;
import com.github.FishMiner.ui.ports.out.domain.interfaces.IGameContext;
import com.github.FishMiner.ui.ports.out.domain.enums.ScreenType;

public class LoginScreen extends AbstractScreen implements IGameScreen {
    private static final String TAG = "LoginScreen";
    private TextField emailField;
    private TextField passwordField;
    private TextField confirmPasswordField;
    private Label statusLabel;
    private Label titleLabel;
    private TextButton submitButton;
    private Label toggleLink;
    private Table dynamicContainer;
    private boolean isLoginMode = true;

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

        initializeUIElements();

        table.add(titleLabel).padBottom(Configuration.getInstance().getLargePadding()).row();
        table.add(emailField).width(Configuration.getInstance().getScreenWidth() / 2)
                .height(emailField.getStyle().font.getData().lineHeight
                        + Configuration.getInstance().getMediumPadding())
                .padBottom(Configuration.getInstance().getMediumPadding()).row();
        table.add(passwordField).width(Configuration.getInstance().getScreenWidth() / 2)
                .padBottom(Configuration.getInstance().getMediumPadding()).row();
        table.add(dynamicContainer).row();

        TextButton backButton = ButtonFactory.createTextButton("Back", ButtonSize.MEDIUM, () -> {
            GameEventBus.getInstance().post(new ChangeScreenEvent(ScreenType.MENU));
        });
        table.add(backButton).size(backButton.getWidth(), backButton.getHeight())
                .padTop(Configuration.getInstance().getMediumPadding()).row();

        renderLoginLayout(); // Initial layout
    }

    private void initializeUIElements() {
        titleLabel = new Label("LOGIN", skin);
        titleLabel.setFontScale(Configuration.getInstance().getLargeFontScale());

        emailField = new TextField("", skin);
        emailField.setMessageText("Email");
        emailField.getStyle().font.getData().setScale(Configuration.getInstance().getSmallFontScale());

        passwordField = new TextField("", skin);
        passwordField.setMessageText("Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        passwordField.getStyle().font.getData().setScale(Configuration.getInstance().getSmallFontScale());

        confirmPasswordField = new TextField("", skin);
        confirmPasswordField.setMessageText("Confirm Password");
        confirmPasswordField.setPasswordMode(true);
        confirmPasswordField.setPasswordCharacter('*');
        confirmPasswordField.getStyle().font.getData().setScale(Configuration.getInstance().getSmallFontScale());
        confirmPasswordField.setVisible(false);

        statusLabel = new Label("", skin);
        statusLabel.setFontScale(Configuration.getInstance().getSmallFontScale());

        submitButton = ButtonFactory.createTextButton("Login", ButtonSize.MEDIUM, () -> {
            String email = emailField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (email.isEmpty() || password.isEmpty() || (!isLoginMode && confirmPassword.isEmpty())) {
                statusLabel.setText("Please fill in all fields.");
                return;
            }

            if (!isLoginMode && !password.equals(confirmPassword)) {
                statusLabel.setText("Passwords do not match.");
                return;
            }

            if (isLoginMode) {
                GameEventBus.getInstance().post(new LoginRequestEvent(email, password));
            } else {
                GameEventBus.getInstance().post(new RegisterUserRequest(email, password));
            }
        });

        toggleLink = new Label("Don't have an account? Register", skin);
        toggleLink.setColor(Color.LIGHT_GRAY);
        toggleLink.setFontScale(Configuration.getInstance().getSmallFontScale());
        toggleLink.setTouchable(Touchable.enabled);
        toggleLink.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isLoginMode = !isLoginMode;
                renderLoginLayout();
            }
        });

        dynamicContainer = new Table();
    }

    private void renderLoginLayout() {
        titleLabel.setText(isLoginMode ? "LOGIN" : "REGISTER");
        submitButton.setText(isLoginMode ? "Login" : "Register");
        toggleLink.setText(isLoginMode
                ? "Don't have an account? Register"
                : "Already have an account? Log in");
        statusLabel.setText("");

        confirmPasswordField.setVisible(!isLoginMode);

        dynamicContainer.clear();
        if (!isLoginMode) {
            dynamicContainer.add(confirmPasswordField).width(Configuration.getInstance().getScreenWidth() / 2)
                    .padBottom(Configuration.getInstance().getMediumPadding()).row();
        }
        dynamicContainer.add(toggleLink).padBottom(Configuration.getInstance().getSmallPadding()).row();
        dynamicContainer.add(statusLabel).padTop(Configuration.getInstance().getSmallPadding()).row();
        dynamicContainer.add(submitButton).size(200, 50).padTop(Configuration.getInstance().getSmallPadding()).row();
    }

    public IGameEventListener<AuthResponseEvent> getAuthResponseListener() {
        return new IGameEventListener<AuthResponseEvent>() {
            @Override
            public void onEvent(AuthResponseEvent event) {
                if (event.isHandled()) return;

                if (event.wasSuccessful()) {
                    Gdx.app.postRunnable(() -> {
                        UserSession.login(event.getEmail());
                        ScreenManager.getInstance().prepareNewScreen(ScreenType.MENU);
                        ScreenManager.getInstance().switchScreenTo(ScreenType.MENU);
                    });
                } else {
                    String errorMessage = event.getError();
                    Logger.getInstance().error(TAG, "Authentication failed: " + errorMessage);

                    Gdx.app.postRunnable(() -> {
                        if (isLoginMode && errorMessage.contains("no user")) {
                            isLoginMode = false;
                            renderLoginLayout();
                            statusLabel.setText("No account found. Please register.");
                        } else {
                            statusLabel.setText("Error: " + errorMessage);
                        }
                    });
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
