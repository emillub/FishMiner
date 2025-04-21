package com.github.FishMiner.common;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Assets {
    private static final String TAG = Assets.class.getSimpleName();
    private static Assets instance;
    private final AssetManager assetManager;

    private final static Skin uiSkin = new Skin(Gdx.files.internal("ui/skin/fishminerSkin.json"));
    private final static String BUTTONS_PATH = "ui/icons/";
    public final static BitmapFont DEFAULT_FONT = uiSkin.getFont("default");
    public final static BitmapFont TITLE_FONT = uiSkin.getFont("title");

    public static Color TITLE_COLOR = Color.BLACK;
    public static Color BLACK = uiSkin.getColor("black");
    public static Color WHITE = uiSkin.getColor("white");
    public static Color LIGHT_BROWN = uiSkin.getColor("maintext");
    public static Color DARK_BROWN = uiSkin.getColor("darkbrown");
    public static Color BACKGROUND_COLOR = Color.valueOf("A1D6F6");

    public static Color POSITIVE_GREEN = new Color(0.5f, 1f, 0.5f, 1f);
    public static Color NEGATIVE_RED = new Color(0.7f, 0f, 0f, 1f);

    public static String PLAYER_TEXTURE = "fisherman.png";
    public static String TITLE_PATH = "ui/title.png";

    public static String TUTORIAL_FOLDER_PATH = "tutorial/";

    public static List<String> tutorialImagePaths;
    private List<Texture> tutorialTextures = new java.util.ArrayList<>();

    private Assets() {
        // Private constructor to prevent instantiation
        assetManager = new AssetManager();
        getTutorialPaths();
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
        PLAY, PAUSE, SETTINGS, LEADERBOARD, SOUND, MUTED, TEXT_BUTTON, TUTORIAL, ARROW
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

    private void getTutorialPaths() {
        FileHandle tutorialFolder = Gdx.files.internal(TUTORIAL_FOLDER_PATH);
        if (!tutorialFolder.exists() || !tutorialFolder.isDirectory()) {
            throw new IllegalStateException("Tutorial folder not found or is not a directory: " + TUTORIAL_FOLDER_PATH);
        }
        FileHandle[] files = tutorialFolder.list();

        // Print each name
        List<String> list = new ArrayList<>();
        for (FileHandle fileHandle : List.of(files)) {
            String path = fileHandle.path();
            System.out.println(path);
            list.add(path);
        }
        tutorialImagePaths = list;
    }

    public void loadTutorialAssets() {
        for (String path : tutorialImagePaths) {
            loadAsset(path, Texture.class);
        }
        finishLoading();
        Logger.getInstance().debug(TAG, "Loaded tutorial assets: " + tutorialTextures.size());
    }

    public List<Texture> getTutorialTextures() {
        if (tutorialTextures.isEmpty()) {
            for (String path : tutorialImagePaths) {
                Texture texture = getAsset(path, Texture.class);
                tutorialTextures.add(texture);
            }
        }
        return tutorialTextures;
    }

    public static Skin getUiskin() {
        return uiSkin;
    }
}
