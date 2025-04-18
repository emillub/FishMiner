package com.github.FishMiner.common;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Assets {
    private static final String TAG = Assets.class.getSimpleName();
    private static Assets instance;
    private AssetManager assetManager;

    private final static Skin uiSkin = new Skin(Gdx.files.internal("ui/skin/fishminerSkin.json"));
    private final static String BUTTONS_PATH = "ui/icons/";
    public final static BitmapFont DEFAULT_FONT = uiSkin.getFont("default");
    public final static BitmapFont TITLE_FONT = uiSkin.getFont("title");

    public static Color TITLE_COLOR = Color.valueOf("5D4037"); // A warm brownish-gray color that complements brown well
    public static Color BLACK = uiSkin.getColor("black");
    public static Color WHITE = uiSkin.getColor("white");
    public static Color LIGHT_BROWN = uiSkin.getColor("maintext");
    public static Color DARK_BROWN = uiSkin.getColor("darkbrown");
    public static Color BACKGROUND_COLOR = Color.valueOf("A1D6F6");

    public static String PLAYER_TEXTURE = "fisherman.png";
    public static String TITLE_PATH = "ui/title.png";

    private Assets() {
        // Private constructor to prevent instantiation
        assetManager = new AssetManager();
    }

    public static Assets getInstance() {
        if (instance == null) {
            instance = new Assets();
        }
        return instance;
    }

    public static final Label.LabelStyle POSI_LABEL_STYLE = new Label.LabelStyle(DEFAULT_FONT,
            new Color(0f, 0.6f, 0f, 1f));
    public static final Label.LabelStyle NEGA_LABEL_STYLE = new Label.LabelStyle(DEFAULT_FONT,
            new Color(0.7f, 0f, 0f, 1f));
    public static final Map<ButtonEnum, Map<ButtonStateEnum, String>> BYTTON_PATHS = new HashMap<>();

    // Sets up image paths
    static {
        for (ButtonEnum button : ButtonEnum.values()){
            BYTTON_PATHS.put(button, new HashMap<>());
            for (ButtonStateEnum state : ButtonStateEnum.values()) {
                String path = BUTTONS_PATH + button.name().toLowerCase() + "-" + state.name().toLowerCase() + ".png";
                BYTTON_PATHS.get(button).put(state, path);
            }
        }
    }

    public enum ButtonStateEnum {
        UP, DOWN
    }

    public enum ButtonEnum {
        PLAY, PAUSE, SETTINGS, LEADERBOARD, SOUND, MUTED, TEXT_BUTTON
    }    

    public String getButtonPath (ButtonEnum button, ButtonStateEnum state) {
        return BYTTON_PATHS.get(button).get(state);
    }

    public void loadAsset(String path, Class<?> type) {
        Logger.getInstance().debug(TAG,"Loading asset: " + path);
        assetManager.load(path, type);
    }


    public <T> T getAsset(String path, Class<T> type) {
        return assetManager.get(path, type);
    }
    public void finishLoading() {
        assetManager.finishLoading();
    }

    public static Skin getUiskin() {
        return uiSkin;
    }
}
