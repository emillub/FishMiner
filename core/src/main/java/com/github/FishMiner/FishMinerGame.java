package com.github.FishMiner;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.github.FishMiner.data.services.ILogInAPI;
import com.github.FishMiner.data.services.ILeaderBoardService;
import com.github.FishMiner.ui.controller.ScreenManager;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

public class FishMinerGame extends Game {
    private final ILogInAPI firebase;
    private final ILeaderBoardService leaderboard;
    private static Music backgroundMusic;
    private static Music playMusic;

    public FishMinerGame(ILogInAPI firebase, ILeaderBoardService leaderboard) {
        this.firebase = firebase;
        this.leaderboard = leaderboard;
    }

    @Override
    public void create() {
        Configuration.getInstance().updateConfiguration();


        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("StartMusic.ogg"));
        playMusic = Gdx.audio.newMusic(Gdx.files.internal("TownTheme.ogg"));

        backgroundMusic.setLooping(true);
        playMusic.setLooping(true);

        float volume = Configuration.getInstance().getMusicVolume();
        backgroundMusic.setVolume(volume);
        playMusic.setVolume(volume);

        if (Configuration.getInstance().isSoundEnabled()) {
            backgroundMusic.play();
        }

        ScreenManager.getInstance(this).showMenu();
    }

    public static void playBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.play();
        }
        if (playMusic != null) {
            playMusic.stop();
        }
    }

    public static void playGameMusic() {
        if (playMusic != null) {
            playMusic.play();
        }
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }

    public static Music getBackgroundMusic() {
        return backgroundMusic;
    }

    public static Music getPlayMusic() {
        return playMusic;
    }

    public ILogInAPI getFirebase() {
        return firebase;
    }
    public ILeaderBoardService getLeaderboard() { return leaderboard; }


}
