package com.github.FishMiner.domain.managers;

import com.badlogic.gdx.audio.Music;
import com.github.FishMiner.common.Assets;
import com.github.FishMiner.common.Configuration;
import com.github.FishMiner.common.Logger;
import com.github.FishMiner.common.ValidateUtil;
import com.github.FishMiner.domain.events.soundEvents.MusicEvent;
import com.github.FishMiner.domain.ports.in.IGameEventListener;

public class MusicManager implements IGameEventListener<MusicEvent> {
    private static final String TAG = MusicManager.class.getSimpleName();
    private final Configuration configuration = Configuration.getInstance();
    private final Assets assets = Assets.getInstance();
    private Music backgroundMusic;
    private Music playMusic;
    private float previousVolume = configuration.getMusicVolume();
    private Music currentlyPlaying;

    private static MusicManager instance;

    private MusicManager() {
        assets.loadAsset(Assets.BACKGROUND_MUSIC_PATH, Music.class);
        assets.loadAsset(Assets.PLAY_MUSIC_PATH, Music.class);
    }

    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }

    public void applyVolume(float volume) {
        ValidateUtil.validateNotNull(volume, "Volume");
        ValidateUtil.validateRange(volume, 0f, 1f, "Volume");
        configuration.setMusicVolume(volume);
        if (currentlyPlaying != null) {
            currentlyPlaying.setVolume(volume);
            Logger.getInstance().debug(TAG, "Volume set to: " + volume);
        }
    }

    private void switchMusic(Music newMusic) {
        if (newMusic == currentlyPlaying) {
            return;
        }
        if (currentlyPlaying != null && currentlyPlaying.isPlaying()) {
            currentlyPlaying.stop();
        }
        currentlyPlaying = newMusic;
        currentlyPlaying.setVolume(configuration.getMusicVolume());
        currentlyPlaying.play();
        Logger.getInstance().debug(TAG, "Switching music to: " + newMusic);
    }

    public void playBackground() {
        if (backgroundMusic == null) {
            backgroundMusic = getBackgroundMusic();
        }
        switchMusic(backgroundMusic);
    }

    public void playGame() {
        if (playMusic == null) {
            playMusic = getPlayMusic();
        }
        switchMusic(playMusic);
    }

    private Music getBackgroundMusic() {
        if (backgroundMusic == null) {
            assets.loadAsset(Assets.BACKGROUND_MUSIC_PATH, Music.class);
            assets.finishLoading();
            backgroundMusic = assets.getAsset(Assets.BACKGROUND_MUSIC_PATH, Music.class);
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(configuration.getMusicVolume());
        }
        return backgroundMusic;
    }

    private Music getPlayMusic() {
        if (playMusic == null) {
            assets.loadAsset(Assets.PLAY_MUSIC_PATH, Music.class);
            assets.finishLoading();
            playMusic = assets.getAsset(Assets.PLAY_MUSIC_PATH, Music.class);
            playMusic.setLooping(true);
            playMusic.setVolume(configuration.getMusicVolume());
        }
        return playMusic;
    }

    private void mute() {
        if (currentlyPlaying != null) {
            previousVolume = configuration.getMusicVolume();
            if (previousVolume > 0f) {
                configuration.setMusicVolume(0f);
                currentlyPlaying.setVolume(0f);
                Logger.getInstance().debug(TAG, "Music muted.");
            }
        }
    }

    private void unMute() {
        if (currentlyPlaying != null) {
            float volumeToRestore = previousVolume > 0f ? previousVolume : Configuration.DEFAULT_MUSIC_VOLUME;
            configuration.setMusicVolume(volumeToRestore);
            currentlyPlaying.setVolume(volumeToRestore);
            Logger.getInstance().debug(TAG, "Music unmuted. Restored volume: " + volumeToRestore);
        }
    }

    private void toggleMute() {
        Logger.getInstance().debug(TAG, "Toggling mute: " + configuration.isMusicEnabled());
        if (configuration.isMusicEnabled()) {
            mute();
        } else {
            unMute();
        }
    }

    @Override
    public void onEvent(MusicEvent event) {
        if (event.isHandled()) return;

        switch (event.getCommand()) {
            case PLAY_BACKGROUND -> playBackground();
            case PLAY_GAME       -> playGame();
            case ADJUST_VOLUME -> applyVolume(event.getVolume());
            case TOGGLE_MUTE -> toggleMute();

        }
        event.setHandled();
    }

    @Override
    public Class<MusicEvent> getEventType() {
        return MusicEvent.class;
    }
}
