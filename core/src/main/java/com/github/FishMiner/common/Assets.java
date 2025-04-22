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
    public static Color TITLE_COLOR = Color.BLACK;
    public static Color BLACK = uiSkin.getColor("black");
    public static Color WHITE = uiSkin.getColor("white");
    public static Color DARK_BROWN = uiSkin.getColor("darkbrown");
    public static Color BACKGROUND_COLOR = Color.valueOf("A1D6F6");

    public static Color POSITIVE_GREEN = new Color(0.5f, 1f, 0.5f, 1f);
    public static Color NEGATIVE_RED = new Color(0.7f, 0f, 0f, 1f);

    public static String PLAYER_TEXTURE = "fisherman.png";
    public static String TITLE_PATH = "ui/title.png";

    public static String BACKGROUND_MUSIC_PATH = "music/StartMusic.ogg";
    public static String PLAY_MUSIC_PATH = "music/TownTheme.ogg";

    // Automatically detect assets folder path
    public static String assetsFolderPath;

    public static String TUTORIAL_FOLDER_PATH = assetsFolderPath + "tutorial/";
    public static HashMap<String, String> tutorialImagePaths = new HashMap<>();

    public static String FISH_TEXTURE_FOLDER_PATH = assetsFolderPath + "fish/";
    public static final HashMap<String, String> fishTexturePaths = new HashMap<>();

    public static String HOOK_TEXTURE_FOLDER_PATH = assetsFolderPath + "hooks/";
    public static final HashMap<String, String> hookTexturePaths = new HashMap<>();

    public static String REEL_TEXTURE_PATH = assetsFolderPath + "reels/";
    public static final HashMap<String, String> reelTexturePaths = new HashMap<>();

    public static String SINKER_TEXTURE_PATH = assetsFolderPath + "sinkers/";
    public static final HashMap<String, String> sinkerTexturePaths = new HashMap<>();

    private Assets() {
        // Private constructor to prevent instantiation
        assetManager = new AssetManager();

        // Automatically detect assets folder path
        // This is a workaround for the issue with Gdx.files.getLocalStoragePath()
        // returning "assets/"
        if (!Gdx.files.internal("assets/").exists()) {
            assetsFolderPath = "";
        } else {
            assetsFolderPath = "assets/";
        }

        PLAYER_TEXTURE = assetsFolderPath + "fisherman.png";
        TITLE_PATH = assetsFolderPath + "ui/title.png";
        BACKGROUND_MUSIC_PATH = assetsFolderPath + "music/StartMusic.ogg";
        PLAY_MUSIC_PATH = assetsFolderPath + "music/TownTheme.ogg";
        TUTORIAL_FOLDER_PATH = assetsFolderPath + "tutorial/";
        FISH_TEXTURE_FOLDER_PATH = assetsFolderPath + "fish/";
        HOOK_TEXTURE_FOLDER_PATH = assetsFolderPath + "hooks/";
        REEL_TEXTURE_PATH = assetsFolderPath + "reels/";
        SINKER_TEXTURE_PATH = assetsFolderPath + "sinkers/";
        System.out.println("Assets folder path: " + assetsFolderPath);
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
        try {
            FileHandle tutorialFolder = Gdx.files.internal(TUTORIAL_FOLDER_PATH);
            populateTexturePaths(tutorialFolder, tutorialImagePaths);
        } catch (Exception e) {
            Logger.getInstance().error(TAG,
                    "Tutorial folder not found. Try setting or removing the assets folder path.");
        }
    }

    private void getFishTexturePaths() {
        try {
            FileHandle fishFolder = Gdx.files.internal(FISH_TEXTURE_FOLDER_PATH);
            populateTexturePaths(fishFolder, fishTexturePaths);
        } catch (Exception e) {
            Logger.getInstance().error(TAG, "Fish folder not found. Try setting or removing the assets folder path.");
        }
    }

    private void getReelTexturePaths() {
        try {
            FileHandle reelFolder = Gdx.files.internal(REEL_TEXTURE_PATH);
            populateTexturePaths(reelFolder, reelTexturePaths);
        } catch (Exception e) {
            Logger.getInstance().error(TAG, "Reel folder not found. Try setting or removing the assets folder path.");
        }
    }

    private void getHookTexturePaths() {
        try {
            FileHandle hookFolder = Gdx.files.internal(HOOK_TEXTURE_FOLDER_PATH);
            populateTexturePaths(hookFolder, hookTexturePaths);
        } catch (Exception e) {
            Logger.getInstance().error(TAG, "Hook folder not found. Try setting or removing the assets folder path.");
        }
    }

    private void getSinkerTexturePaths() {
        FileHandle sinkerFolder = Gdx.files.internal(SINKER_TEXTURE_PATH);
        if (!sinkerFolder.exists() || !sinkerFolder.isDirectory()) {
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
        List<Map.Entry<String, String>> sortedEntries = new ArrayList<>(tutorialImagePaths.entrySet());
        // Sort the entries based on the numeric part of the file name
        sortedEntries.sort((entry1, entry2) -> {
            String fileName1 = entry1.getKey();
            String fileName2 = entry2.getKey();
            try {
                int num1 = Integer.parseInt(fileName1.replaceAll("\\D", ""));
                int num2 = Integer.parseInt(fileName2.replaceAll("\\D", ""));
                return Integer.compare(num1, num2);
            } catch (NumberFormatException e) {
                return fileName1.compareTo(fileName2);
            }
        });

        List<Texture> tutorialTextures = new ArrayList<>(sortedEntries.size());
        for (Map.Entry<String, String> entry : sortedEntries) {
            String path = entry.getValue();
            if (assetManager.isLoaded(path, Texture.class)) {
                tutorialTextures.add(assetManager.get(path, Texture.class));
            }
        }
        return tutorialTextures;
    }

    public static Skin getUiskin() {
        return uiSkin;
    }
}
