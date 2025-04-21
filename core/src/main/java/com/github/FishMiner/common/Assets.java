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

public class Assets {
    private static final String TAG = Assets.class.getSimpleName();
    private static Assets instance;
    private final AssetManager assetManager;

    // Frequently used assets

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

    public static String BACKGROUND_MUSIC_PATH = "music/StartMusic.ogg";
    public static String PLAY_MUSIC_PATH = "music/TownTheme.ogg";

    public static String TUTORIAL_FOLDER_PATH = "assets/tutorial/";
    public static HashMap<String, String> tutorialImagePaths = new HashMap<>();

    public static final String FISH_TEXTURE_FOLDER_PATH = "assets/fish/";
    public static final HashMap<String, String> fishTexturePaths = new HashMap<>();

    public static final String HOOK_TEXTURE_FOLDER_PATH = "assets/hooks/";
    public static final HashMap<String, String> hookTexturePaths = new HashMap<>();

    public static final String REEL_TEXTURE_PATH = "assets/reels/";
    public static final HashMap<String, String> reelTexturePaths = new HashMap<>();

    public static final String SINKER_TEXTURE_PATH = "assets/sinkers/";
    public static final HashMap<String, String> sinkerTexturePaths = new HashMap<>();

    private Assets() {
        // Private constructor to prevent instantiation
        assetManager = new AssetManager();
        getTutorialPaths();
        getFishTexturePaths();
        getReelTexturePaths();
        getHookTexturePaths();
        getSinkerTexturePaths();
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

    public static String getButtonPath(ButtonEnum button, ButtonStateEnum state) {
        BYTTON_PATHS.computeIfAbsent(button, b -> new HashMap<>());
        BYTTON_PATHS.get(button).computeIfAbsent(state,
                s -> BUTTONS_PATH + button.name().toLowerCase() + "-" + state.name().toLowerCase() + ".png");
        return BYTTON_PATHS.get(button).get(state);
    }

    public void loadAsset(String path, Class<?> type) {
        if (assetManager.isLoaded(path, type)) {
            Logger.getInstance().debug(TAG, "Asset already loaded: " + path);
            return;
        }
        Logger.getInstance().debug(TAG, "Loading asset: " + path);
        assetManager.load(path, type);
    }


    public <T> T getAsset(String path, Class<T> type) {
        if (!assetManager.isLoaded(path, type)) {
            Logger.getInstance().debug(TAG, "Asset not loaded: " + path);
            loadAsset(path, type);
            return null;
        }
        return assetManager.get(path, type);
    }
    public void finishLoading() {
        assetManager.finishLoading();
    }

    private void getTutorialPaths() {
        FileHandle tutorialFolder = Gdx.files.internal(TUTORIAL_FOLDER_PATH);
        if (!tutorialFolder.exists() || !tutorialFolder.isDirectory()) {
            TUTORIAL_FOLDER_PATH = ensureCorrectFolderPath(TUTORIAL_FOLDER_PATH);
            tutorialFolder = Gdx.files.internal(TUTORIAL_FOLDER_PATH);
        }

        populateTexturePaths(tutorialFolder, tutorialImagePaths);
    }

    private String ensureCorrectFolderPath(String folderPath) {

        if (!folderPath.startsWith("assets/")) {
            folderPath.replace("assets/", "");
        } else {
            folderPath = "assets/" + folderPath;
        }
        return folderPath;
    }

    private void getFishTexturePaths() {
        FileHandle fishFolder = Gdx.files.internal(FISH_TEXTURE_FOLDER_PATH);
        if (!fishFolder.exists() || !fishFolder.isDirectory()) {
            fishFolder = Gdx.files.internal(ensureCorrectFolderPath(FISH_TEXTURE_FOLDER_PATH));
        }
        populateTexturePaths(fishFolder, fishTexturePaths);

    }

    private void getReelTexturePaths() {
        FileHandle reelFolder = Gdx.files.internal(REEL_TEXTURE_PATH);
        if (!reelFolder.exists() || !reelFolder.isDirectory()) {
            reelFolder = Gdx.files.internal(ensureCorrectFolderPath(REEL_TEXTURE_PATH));
        }
        populateTexturePaths(reelFolder, reelTexturePaths);
    }

    private void getHookTexturePaths() {
        FileHandle hookFolder = Gdx.files.internal(HOOK_TEXTURE_FOLDER_PATH);
        if (!hookFolder.exists() || !hookFolder.isDirectory()) {
            hookFolder = Gdx.files.internal(ensureCorrectFolderPath(HOOK_TEXTURE_FOLDER_PATH));
        }
        populateTexturePaths(hookFolder, hookTexturePaths);
    }

    private void getSinkerTexturePaths() {
        FileHandle sinkerFolder = Gdx.files.internal(SINKER_TEXTURE_PATH);
        if (!sinkerFolder.exists() || !sinkerFolder.isDirectory()) {
            sinkerFolder = Gdx.files.internal(ensureCorrectFolderPath(SINKER_TEXTURE_PATH));
        }
        populateTexturePaths(sinkerFolder, sinkerTexturePaths);
    }

    private void populateTexturePaths(FileHandle folder, HashMap<String, String> texturePaths) {
        for (FileHandle fileHandle : folder.list()) {
            String fileName = fileHandle.name().replace(".png", "");
            String path = fileHandle.path();
            texturePaths.put(fileName, path);
        }
    }

    public void loadGameTextures() {
        for (String path : fishTexturePaths.values()) {
            if (!assetManager.isLoaded(path, Texture.class)) {
                loadAsset(path, Texture.class);
            }
        }
        if (!assetManager.isLoaded(PLAYER_TEXTURE, Texture.class)) {
            loadAsset(PLAYER_TEXTURE, Texture.class);
        }
        if (!assetManager.isLoaded(TITLE_PATH, Texture.class)) {
            loadAsset(TITLE_PATH, Texture.class);
        }
    }

    public static String getFishTexturePath(String fishType) {
        return fishTexturePaths.computeIfAbsent(fishType, key -> {
            throw new IllegalArgumentException("Texture path not found for fish type: " + key);
        });
    }

    public static String getHookTexturePath(String hookType) {
        return hookTexturePaths.computeIfAbsent(hookType, key -> {
            throw new IllegalArgumentException("Texture path not found for hook type: " + key);
        });
    }

    public static String getReelTexturePath(String reelType) {
        return reelTexturePaths.computeIfAbsent(reelType, key -> {
            throw new IllegalArgumentException("Texture path not found for reel type: " + key);
        });
    }

    public static String getSinkerTexturePath(String sinkerType) {
        String texturePath = sinkerTexturePaths.get(sinkerType);
        if (texturePath == null) {
            throw new IllegalArgumentException("Texture path not found for sinker type: " + sinkerType);
        }
        return texturePath;
    }

    public void loadFishTextures() {
        for (String path : fishTexturePaths.values()) {
            loadAsset(path, Texture.class);
        }
    }

    public void loadTutorialAssets() {
        for (String path : tutorialImagePaths.values()) {
            loadAsset(path, Texture.class);
        }
    }

    public List<Texture> getTutorialTextures() {
        List<Texture> tutorialTextures = new ArrayList<>(tutorialImagePaths.size());
        for (String path : tutorialImagePaths.values()) {
            if (assetManager.isLoaded(path, Texture.class)) {
                tutorialTextures.add(assetManager.get(path, Texture.class));
            }
        }
        return tutorialTextures;
    }

    public static Skin getUiskin() {
        return uiSkin;
    }

    public void dispose() {
        assetManager.dispose();
    }
}
