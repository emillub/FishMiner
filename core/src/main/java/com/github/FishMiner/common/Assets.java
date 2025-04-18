package com.github.FishMiner.common;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Assets {
    private static final String TAG = Assets.class.getSimpleName();
    private static Assets instance;
    private AssetManager assetManager;

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

    private final static Skin uiSkin = new Skin(Gdx.files.internal("ui/fishminerSkin.json"));
    private final static String BUTTONS_PATH = "ui/icons/";

    public static final Label.LabelStyle POSI_LABEL_STYLE = new Label.LabelStyle(uiSkin.getFont("default"), new Color(0f, 0.6f, 0f, 1f));
    public static final Label.LabelStyle NEGA_LABEL_STYLE = new Label.LabelStyle(uiSkin.getFont("default"), new Color(0.7f, 0f, 0f, 1f));
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
        PLAY, PAUSE, SETTINGS, LEADERBOARD, SOUND, MUTED
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
}
